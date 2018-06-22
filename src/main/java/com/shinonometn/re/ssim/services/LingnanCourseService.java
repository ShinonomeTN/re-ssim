package com.shinonometn.re.ssim.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinonometn.re.ssim.caterpillar.SpiderMonitor;
import com.shinonometn.re.ssim.caterpillar.SpiderStatus;
import com.shinonometn.re.ssim.caterpillar.kingo.KingoUrls;
import com.shinonometn.re.ssim.caterpillar.kingo.capture.*;
import com.shinonometn.re.ssim.caterpillar.kingo.pojo.Course;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.models.CaptureTask;
import com.shinonometn.re.ssim.models.CaptureTaskDTO;
import com.shinonometn.re.ssim.repository.CaptureTaskRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LingnanCourseService {

    private final Site site = Site.me()
            .setDomain("jwgl.lnc.edu.cn")
            .setTimeOut(5000)
            .setRetryTimes(3)
            .setSleepTime(500);

    private final CacheService cacheService;
    private final CaptureTaskRepository captureTaskRepository;
    private final SpiderMonitor spiderMonitor;

    private final Properties caterpillarProperties;

    @Autowired
    public LingnanCourseService(CacheService cacheService,
                                CaptureTaskRepository captureTaskRepository,
                                SpiderMonitor spiderMonitor,
                                @Qualifier("caterpillarProperties")
                                        Properties caterpillarProperties) {

        this.cacheService = cacheService;
        this.captureTaskRepository = captureTaskRepository;
        this.spiderMonitor = spiderMonitor;
        this.caterpillarProperties = caterpillarProperties;

        // Setup
        this.site.setUserAgent(caterpillarProperties.getProperty("user.agent"))
                .setCharset(caterpillarProperties.getProperty("encoding"));

    }

    /**
     * Get all school terms
     * <p>
     * if cache not found, load from remote and cache it
     *
     * @return a map, term code as key, term name as value
     */
    public Map<String, String> getTermList() {

        Map<String, String> cachedResult = cacheService.get(CacheKeys.TERM_LIST, new TypeReference<Map<String, String>>() {
        });
        if (cachedResult != null) {
            return cachedResult;
        }

        final Map<String, String> capturedResult = new HashMap<>();

        Spider.create(new TermListPageProcessor(site))
                .addUrl(KingoUrls.classInfoQueryPage)
                .addPipeline((r, t) -> capturedResult.putAll(r.get(TermListPageProcessor.FIELD_TERMS)))
                .run();

        cacheService.put(CacheKeys.TERM_LIST, capturedResult);

        return capturedResult;
    }

    /**
     * Individual cache and re-capture list from remote
     *
     * @return see getTermList()
     */
    public Map<String, String> reloadAndGetTermList() {
        cacheService.expire(CacheKeys.TERM_LIST);
        return getTermList();
    }

    /**
     * Get all tasks
     *
     * @return task list
     */
    public List<CaptureTaskDTO> listTasks() {
        return captureTaskRepository.findAllProjectedBy();
    }

    /**
     * Create a term capture task with code
     *
     * @param termCode term code
     * @return created task
     */
    public CaptureTask createTask(@NotNull String termCode) {
        CaptureTask captureTask = new CaptureTask();
        captureTask.setCreateDate(new Date());
        captureTask.setTermCode(termCode);
        captureTask.setTermName(getTermList().get(termCode));

        return captureTaskRepository.save(captureTask);
    }

    /**
     * Stop a task
     *
     * @param taskId task id
     * @return task dto
     */
    public CaptureTaskDTO stopTask(String taskId) {
        Optional<CaptureTask> captureTaskResult = captureTaskRepository.findById(taskId);
        if (!captureTaskResult.isPresent()) return null;

        SpiderStatus spiderStatus = spiderMonitor.getSpiderStatus().get(taskId);
        spiderStatus.stop();

        return captureTaskRepository.findProjectedById(taskId);
    }

    /**
     * Start a capture task by task id
     * <p>
     * It will capture all subject info to a temporal folder
     *
     * @param taskId
     * @return
     */
    public CaptureTaskDTO startTask(String taskId) {
        Optional<CaptureTask> captureTaskResult = captureTaskRepository.findById(taskId);
        if (!captureTaskResult.isPresent()) return null;

        CaptureTaskDTO dto = captureTaskRepository.findProjectedById(taskId);

        if (dto.getSpiderStatus() != null && dto.getSpiderStatus().getStatus().equals("Running"))
            throw new IllegalStateException("task_is_running");

        if (notLogin() && !doLogin()) {
            throw new IllegalStateException("login_to_kingo_failed");
        }

        File tempFolder = getTempDir(taskId);
        ObjectMapper objectMapper = new ObjectMapper();
        Spider spider = Spider.create(new CourseDetailsPageProcessor(site))
                .addPipeline((resultItems, task) -> {
                    try {
                        Course course = resultItems.get("subject");
                        objectMapper.writeValue(new FileOutputStream(new File(tempFolder, course.getCode())), course);
                    } catch (Exception ignore) {

                    }
                })
                .setUUID(taskId)
                .thread(Integer.parseInt(caterpillarProperties.getProperty("threads")));

        spider.startRequest(fetchTermList(captureTaskResult.get().getTermCode()).stream()
                .map(id -> createSubjectRequest(captureTaskResult.get().getTermCode(), id))
                .collect(Collectors.toList()));

        spiderMonitor.register(spider);

        spider.start();

        return dto;
    }

    /**
     * Get task status from spider status
     *
     * @param captureTask task
     * @return task status if exist, otherwise null
     */
    public SpiderStatus getStatusByTask(CaptureTask captureTask) {
        if (captureTask.getId() == null) return null;
        return spiderMonitor.getSpiderStatus().get(captureTask.getId());
    }


    /**
     * Private procedure
     */

    private Collection<String> fetchTermList(String termCode) {
        Map<String, String> termList = new HashMap<>();

        Spider.create(new CoursesListPageProcessor(site))
                .addUrl(KingoUrls.subjectListQueryPath + termCode)
                .addPipeline((resultItems, task) -> termList.putAll(resultItems.get("courses")))
                .run();

        return termList.keySet();
    }

    private File getTempDir(String taskId) {
        File file = new File("./_temp/" + taskId);
        if (!file.exists()) if (!file.mkdirs()) throw new IllegalStateException("create_temp_folder_failed");
        File[] files = file.listFiles();
        if (files != null) Stream.of(files).forEach(File::delete);
        return file;
    }

    private boolean notLogin() {
        synchronized (site) {
            AtomicBoolean isLogin = new AtomicBoolean(false);

            Request request = new Request(KingoUrls.classInfoQueryPage);
            request.addHeader("Referer", KingoUrls.classInfoQueryPage);

            Spider.create(new LoginStatusPageProcessor(site))
                    .addRequest(request)
                    .addPipeline((resultItems, task) -> isLogin.set(resultItems.get("isLogin"))).run();

            return !isLogin.get();
        }
    }

    private boolean doLogin() {
        String username = caterpillarProperties.getProperty("username");
        String password = caterpillarProperties.getProperty("password");
        String role = caterpillarProperties.getProperty("role");

        Map<String, Object> items = new HashMap<>();

        Spider.create(new LoginPreparePageProcessor(username, password, role, site))
                .addUrl(KingoUrls.loginPageAddress)
                .addPipeline((r, t) -> items.putAll(r.getAll()))
                .run();

        AtomicBoolean loginResult = new AtomicBoolean(false);

        if ((Boolean) items.get("ready")) {

            if(items.get("cookies") != null){
                Map<String, String> cookies = ((List<String>) items.get("cookies"))
                        .stream()
                        .flatMap(s -> Stream.of(s.split(";")))
                        .flatMap(s -> Stream.of(new String[][]{s.split("=")}))
                        .collect(Collectors.toMap(ss -> ss[0], ss -> ss[1]));

                this.site.addCookie("ASP.NET_SessionId", cookies.get("ASP.NET_SessionId"));
            }

            Map<String, Object> formFields = (Map<String, Object>) items.get("formFields");

            Request loginRequest = new Request(KingoUrls.loginPageAddress);
            loginRequest.setMethod(HttpConstant.Method.POST);
            loginRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
            loginRequest.addHeader("Referer",KingoUrls.loginPageAddress);
            loginRequest.setRequestBody(HttpRequestBody.form(formFields, caterpillarProperties.getProperty("encoding")));


            Spider.create(new LoginExecutePageProcessor(site))
                    .addRequest(loginRequest)
                    .addPipeline((resultItems, task) -> loginResult.set(resultItems.get("loginResult")))
                    .run();

        }

        return loginResult.get();

    }

    private Request createSubjectRequest(String termCode, String subjectCode) {
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

}
