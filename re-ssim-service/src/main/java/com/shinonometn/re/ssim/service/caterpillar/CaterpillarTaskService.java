package com.shinonometn.re.ssim.service.caterpillar;

import com.shinonometn.re.ssim.commons.BusinessException;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.commons.JSON;
import com.shinonometn.re.ssim.commons.file.fundation.FileContext;
import com.shinonometn.re.ssim.service.caterpillar.commons.CaptureTaskStage;
import com.shinonometn.re.ssim.service.caterpillar.commons.CaterpillarMonitorPlugin;
import com.shinonometn.re.ssim.service.caterpillar.entity.CaptureTask;
import com.shinonometn.re.ssim.service.caterpillar.entity.CaptureTaskDetails;
import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting;
import com.shinonometn.re.ssim.service.caterpillar.kingo.KingoUrls;
import com.shinonometn.re.ssim.service.caterpillar.kingo.capture.*;
import com.shinonometn.re.ssim.service.caterpillar.kingo.pojo.Course;
import com.shinonometn.re.ssim.service.caterpillar.repository.CaptureTaskRepository;
import com.shinonometn.re.ssim.service.courses.CourseInfoService;
import com.shinonometn.re.ssim.service.courses.entity.CourseEntity;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CaterpillarTaskService {

    private final Logger logger = LoggerFactory.getLogger(CaterpillarTaskService.class);

    private final CourseInfoService courseInfoService;
    private final CaterpillarFileManageService fileManageService;
    private final SpiderMonitor spiderMonitor;
    private final TaskExecutor taskExecutor;

    private final CaterpillarMonitorPlugin caterpillarMonitorPlugin;

    private final CaptureTaskRepository captureTaskRepository;

    public CaterpillarTaskService(CourseInfoService courseInfoService, CaterpillarFileManageService fileManageService,
                                  SpiderMonitor spiderMonitor,
                                  TaskExecutor taskExecutor,
                                  CaterpillarMonitorPlugin caterpillarMonitorPlugin,
                                  CaptureTaskRepository captureTaskRepository) {
        this.courseInfoService = courseInfoService;

        this.fileManageService = fileManageService;
        this.spiderMonitor = spiderMonitor;
        this.taskExecutor = taskExecutor;
        this.caterpillarMonitorPlugin = caterpillarMonitorPlugin;
        this.captureTaskRepository = captureTaskRepository;
    }

    /**
     * Get all school terms
     * <p>
     * if cache not found, load from remote and cache it
     *
     * @return a map, term code as key, term name as value
     */
    @Cacheable(CacheKeys.CAPTURE_TERM_LIST)
    @NotNull
    public Map<String, String> getTermList() {

        final Map<String, String> capturedResult = new HashMap<>();

        Spider.create(new TermListPageProcessor(CaterpillarSetting.Companion.createDefaultSite()))
                .addUrl(KingoUrls.classInfoQueryPage)
                .addPipeline((r, t) -> capturedResult.putAll(TermListPageProcessor.getTerms(r)))
                .run();

        logger.debug("Cache not found, returning remote data.");

        return capturedResult;
    }

    /**
     * Individual cache and re-capture list from remote
     *
     * @return see getTermList()
     */
    @CachePut(CacheKeys.CAPTURE_TERM_LIST)
    public Map<String, String> reloadAndGetTermList() {
        return getTermList();
    }

    /**
     * Get all tasks
     *
     * @return task list
     */
    public Page<CaptureTaskDetails> listTasks(Pageable pageable) {
        return captureTaskRepository.findAll(pageable).map(this::getTaskDetails);
    }

    public CaptureTaskDetails taskDetails(String id) {
        return captureTaskRepository.findById(id).map(this::getTaskDetails).orElse(null);
    }

    /**
     * Create a term capture task with code
     *
     * @param termCode term code
     * @return created task
     */
    public CaptureTask createTask(String termCode) {
        CaptureTask captureTask = new CaptureTask();

        captureTask.setCreateDate(new Date());
        captureTask.setTermCode(termCode);
        captureTask.setTermName(getTermList().get(termCode));
        captureTask.setStage(CaptureTaskStage.INITIALIZE);

        return captureTaskRepository.save(captureTask);
    }

    /**
     * Stop a task
     *
     * @param taskId task id
     * @return task dto
     */
    public CaptureTaskDetails stopTask(String taskId) {
        CaptureTask captureTask = captureTaskRepository.findById(taskId).orElse(null);
        if (captureTask == null) return null;

        SpiderStatus spiderStatus = spiderMonitor.getSpiderStatus().get(taskId);
        if (spiderStatus == null) throw new BusinessException("task_have_not_initialized");
        spiderStatus.stop();

        return captureTaskRepository.findById(taskId).map(this::getTaskDetails).orElse(null);
    }

    /**
     * Resume a stopped task
     *
     * @param taskId task id
     * @return dto
     */
    public CaptureTaskDetails resumeTask(String taskId) {
        CaptureTaskDetails captureTaskDetails = captureTaskRepository.findById(taskId).map(this::getTaskDetails).orElse(null);
        if (captureTaskDetails == null) return null;

        SpiderStatus spiderStatus = captureTaskDetails.getRunningTaskStatus();
        if (spiderStatus == null) throw new BusinessException("task_have_not_initialized");

        spiderStatus.start();

        return captureTaskDetails;
    }

    /**
     * Start a capture task by task id
     * <p>
     * It will capture all subject info to a temporal folder
     *
     * @param taskId task id
     * @return dto
     */
    public CaptureTaskDetails startTask(String taskId, CaterpillarSetting caterpillarSetting) {

        CaptureTaskDetails captureTaskDetails = captureTaskRepository
                .findById(taskId)
                .map(this::getTaskDetails)
                .orElseThrow(() -> new BusinessException("task_not_exists"));

        if (captureTaskDetails.getRunningTaskStatus() != null) throw new IllegalStateException("spider_exist");

        Site site = doLogin(caterpillarSetting);

        FileContext dataFolder = fileManageService.contextOf(taskId);

        Spider spider = Spider.create(new CourseDetailsPageProcessor(site))
                .addPipeline((resultItems, task) -> {
                    try {
                        Course course = CourseDetailsPageProcessor.getSubject(resultItems);
                        JSON.write(new FileOutputStream(new File(dataFolder.getFile(), Objects.requireNonNull(course.getCode()))), course);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .setUUID(taskId)
                .thread(caterpillarSetting.getThreads());

        spider.startRequest(fetchTermList(site, captureTaskDetails.getTaskInfo().getTermCode())
                .stream()
                .map(id -> createSubjectRequest(site, captureTaskDetails.getTaskInfo().getTermCode(), id))
                .collect(Collectors.toList()));

        spiderMonitor.register(spider);

        spider.start();

        captureTaskDetails.getTaskInfo().setStage(CaptureTaskStage.CAPTURE);
        captureTaskRepository.save(captureTaskDetails.getTaskInfo());

        return captureTaskDetails;
    }


    /**
     * Check if caterpillar setting valid
     *
     * @param caterpillarSetting settings
     * @return result
     */
    public boolean isSettingValid(CaterpillarSetting caterpillarSetting) {
        return doLogin(caterpillarSetting) != null;
    }

    /**
     * Delete a not running task
     *
     * @param id task id
     */
    public void deleteTask(String id) {
        Map<String, SpiderStatus> spiderStatusMap = spiderMonitor.getSpiderStatus();

        if (spiderStatusMap.containsKey(id)) {

            SpiderStatus spiderStatus = spiderStatusMap.get(id);
            if (spiderStatus.getStatus().equals("Running")) throw new BusinessException("spider_running");

            spiderStatusMap.remove(id);
        }

        FileContext fileContext = fileManageService.contextOf(id);
        if(fileContext.exists()) //noinspection ResultOfMethodCallIgnored
            fileContext.getFile().delete();

        captureTaskRepository.deleteById(id);
    }

    /**
     * Start importing data to database
     *
     * @param taskId taskId
     * @return task base info
     */
    @CacheEvict({
            CacheKeys.TERM_COURSE_LIST
    })
    @NotNull
    public CaptureTask importSubjectData(String taskId) {

        CaptureTask captureTask = captureTaskRepository.findById(taskId).orElse(null);
        if (captureTask == null) throw new BusinessException("task_not_found");

        Runnable importTask = () -> {
            logger.info("Importing of {} started.", captureTask.getId());
            caterpillarMonitorPlugin.increaseCaptureTaskCount();

            mongoTemplate.getCollection(mongoTemplate.getCollectionName(CourseEntity.class))
                    .deleteMany(new Document("term", captureTask.getTermName()));
            logger.info("Records of {} deleted. If task failed, it will not be revert.", captureTask.getTermName());

            ArrayList<String> succeedEntity = new ArrayList<>();

            try {
                File folder = fileManageService.contextOf(taskId).getFile();
                if (!folder.isDirectory()) throw new BusinessException("temp_dir_not_found");

                for (File file : Objects.requireNonNull(folder.listFiles())) {
                    CourseEntity courseEntity = JSON.read(new FileInputStream(file), CourseEntity.class);

                    succeedEntity.add(courseInfoService.save(courseEntity).getId());
                }

                captureTask.setFinished(true);
                captureTaskRepository.save(captureTask);

                logger.info("Importing of {} succeed", captureTask.getId());

            } catch (IOException e) {
                logger.error("Something happen while importing files, reversing...", e);
                succeedEntity.forEach(courseInfoService::delete);
                logger.warn("Reversing of task {} completed.", captureTask.getId());
            } finally {
                caterpillarMonitorPlugin.decreaseCaptureTaskCount();
            }

        };

        taskExecutor.execute(importTask);
        captureTask.setStage(CaptureTaskStage.IMPORT);
        captureTaskRepository.save(captureTask);
        return captureTask;
    }

    /*

        Status

    */

    /**
     * Get importing task count
     *
     * @return int
     */
    public Integer getImportingTaskCount() {
        return caterpillarMonitorPlugin.getImportTaskCount();
    }

    /**
     * Get running spider counts
     *
     * @return long
     */
    public long getCapturingTaskCount() {
        return spiderMonitor.getSpiderStatus().values().stream().filter(i -> i.getStatus().equals("Running")).count();
    }

    /**
     * Get a task dto by id
     *
     * @param id id
     * @return dto
     */
    @Nullable
    public CaptureTaskDetails queryTask(@NotNull String id) {
        return captureTaskRepository.findById(id).map(this::getTaskDetails).orElse(null);
    }

    /*

      Private procedure

     */

    private CaptureTaskDetails getTaskDetails(CaptureTask captureTask) {
        CaptureTaskDetails captureTaskDetails = new CaptureTaskDetails();
        captureTaskDetails.setTaskInfo(captureTask);
        captureTaskDetails.setRunningTaskStatus(spiderMonitor.getSpiderStatus().get(captureTask.getId()));
        return captureTaskDetails;
    }

    private Collection<String> fetchTermList(Site site, String termCode) {
        Map<String, String> termList = new HashMap<>();

        Spider.create(new CoursesListPageProcessor(site))
                .addUrl(KingoUrls.subjectListQueryPath + termCode)
                .addPipeline((resultItems, task) -> termList.putAll(CoursesListPageProcessor.getCourseList(resultItems)))
                .run();

        logger.debug("Fetched remote course list of term {}.", termCode);

        return termList.keySet();
    }

    private File getTempDir(String taskId) {
        File file = new File("./_temp/" + taskId);
        if (!file.exists()) if (!file.mkdirs()) throw new IllegalStateException("create_temp_folder_failed");
        File[] files = file.listFiles();
        if (files != null) Stream.of(files).forEach(File::delete);
        return file;
    }

    private Site doLogin(CaterpillarSetting caterpillarSetting) {

        Site site = caterpillarSetting.createSite();

        String username = caterpillarSetting.getUsername();
        String password = caterpillarSetting.getPassword();
        String role = caterpillarSetting.getRole();


        Map<String, Object> items = new HashMap<>();

        Spider.create(new LoginPreparePageProcessor(username, password, role, site))
                .addUrl(KingoUrls.loginPageAddress)
                .addPipeline((r, t) -> items.putAll(r.getAll()))
                .run();

        AtomicBoolean loginResult = new AtomicBoolean(false);

        if (!LoginPreparePageProcessor.getIsReady(items)) throw new IllegalStateException("could_not_get_login_form");

        Map<String, String> cookies = LoginPreparePageProcessor.getCookie(items);
        if (cookies != null) site.addCookie("ASP.NET_SessionId", cookies.get("ASP.NET_SessionId"));

        Map<String, Object> formFields = LoginPreparePageProcessor.getFormFields(items);

        Request loginRequest = new Request(KingoUrls.loginPageAddress);
        loginRequest.setMethod(HttpConstant.Method.POST);
        loginRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        loginRequest.addHeader("Referer", KingoUrls.loginPageAddress);
        loginRequest.setRequestBody(HttpRequestBody.form(formFields, Objects.requireNonNull(caterpillarSetting.getEncoding())));


        Spider.create(new LoginExecutePageProcessor(site))
                .addRequest(loginRequest)
                .addPipeline((resultItems, task) -> loginResult.set(LoginExecutePageProcessor.getIsLogin(resultItems)))
                .run();

        logger.debug("Login to kingo {}.", (loginResult.get() ? "successful" : "failed"));
        if (!loginResult.get()) throw new IllegalStateException("login_to_kingo_failed");

        return site;

    }

    private Request createSubjectRequest(Site site, String termCode, String subjectCode) {
        Map<String, Object> form = new HashMap<>();
        form.put("gs", "2");
        form.put("txt_yzm", "");
        form.put("Sel_XNXQ", termCode);
        form.put("Sel_KC", subjectCode);

        Request request = new Request(KingoUrls.subjectQueryPage);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(form, site.getCharset()));
        request.addHeader("Referer", KingoUrls.classInfoQueryPage);

        return request;

    }

    @NotNull
    public Map<String,String> dashBoard() {
        return caterpillarMonitorPlugin.getAll();
    }
}
