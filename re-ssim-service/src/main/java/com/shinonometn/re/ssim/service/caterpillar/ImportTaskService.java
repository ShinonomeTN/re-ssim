package com.shinonometn.re.ssim.service.caterpillar;

import com.shinonometn.re.ssim.commons.BusinessException;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.commons.file.fundation.FileContext;
import com.shinonometn.re.ssim.service.caterpillar.commons.ImportTaskStatus;
import com.shinonometn.re.ssim.service.caterpillar.entity.CaptureTask;
import com.shinonometn.re.ssim.service.caterpillar.entity.ImportTask;
import com.shinonometn.re.ssim.service.caterpillar.plugin.CaterpillarMonitorStore;
import com.shinonometn.re.ssim.service.caterpillar.repository.CaptureTaskRepository;
import com.shinonometn.re.ssim.service.caterpillar.repository.ImportTaskRepository;
import com.shinonometn.re.ssim.service.caterpillar.task.CourseDataImportTask;
import com.shinonometn.re.ssim.service.courses.CourseInfoService;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ImportTaskService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CaptureTaskRepository captureTaskRepository;
    private final ImportTaskRepository importTaskRepository;
    private final MongoTemplate mongoTemplate;

    private final CaterpillarMonitorStore caterpillarMonitorStore;
    private final CaterpillarFileManageService fileManageService;

    private final CourseInfoService courseInfoService;

    private final TaskExecutor taskExecutor;

    public ImportTaskService(CaptureTaskRepository captureTaskRepository,
                             ImportTaskRepository importTaskRepository,
                             MongoTemplate mongoTemplate, CaterpillarFileManageService fileManageService,
                             CaterpillarMonitorStore caterpillarMonitorStore,
                             CourseInfoService courseInfoService,
                             TaskExecutor taskExecutor) {

        this.captureTaskRepository = captureTaskRepository;
        this.importTaskRepository = importTaskRepository;
        this.mongoTemplate = mongoTemplate;
        this.fileManageService = fileManageService;
        this.caterpillarMonitorStore = caterpillarMonitorStore;
        this.courseInfoService = courseInfoService;
        this.taskExecutor = taskExecutor;
    }

    public Page<ImportTask> list(Pageable pageable) {
        return importTaskRepository.findAll(pageable);
    }

    public ImportTask save(ImportTask importTask) {
        return importTaskRepository.save(importTask);
    }

    public void delete(String taskId){
        ImportTask importTask = importTaskRepository
                .findById(taskId)
                .orElseThrow(() -> new BusinessException("import_task_not_exists"));

        if(importTask.getStatus().equals(ImportTaskStatus.IMPORTING))
            throw new BusinessException("import_task_running");

        importTaskRepository.delete(importTask);

        if(importTask.getCaptureTaskId() == null || !captureTaskRepository.existsById(importTask.getCaptureTaskId())) {
            try {
                FileUtils.deleteDirectory(fileManageService.contextOf(importTask.getCaptureTaskId()).getFile());
            } catch (IOException e) {
                logger.warn("Could not delete directory for import task " + taskId);
            }
        }
    }

    public boolean isCaptureTaskRelated(String captureTaskId) {
        return importTaskRepository.existsByCaptureTaskId(captureTaskId);
    }

    @SuppressWarnings("ConstantConditions")
    public String latestVersionOf(String termName) {
        // Get latest version from finished import tasks
        String result = mongoTemplate.query(ImportTask.class)
                .matching(query(where("termName").is(termName).and("finishDate").ne(null)))
                .stream()
                .max(Comparator.comparingLong(l -> l.getFinishDate().getTime()))
                .orElse(null)
                .getId();
        if (result != null) return result;

        // If not found, find from exists courses
        // Because version id is batchId and it is UUID, so sort the
        // list and the latest item normally is the new version code
        result = courseInfoService.executeAggregation(newAggregation(
                project("term", "batchId"),
                match(where("term").is(termName)),
                group("term").addToSet("batchId").as("versions"),
                project("versions")))
                .getUniqueMappedResult()
                .get("versions", new HashSet<String>())
                .stream()
                .max(Comparator.naturalOrder())
                .orElse("");

        return result;
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
    public CaptureTask start(String taskId) {

        CaptureTask captureTask = captureTaskRepository.findById(taskId).orElse(null);
        if (captureTask == null) throw new BusinessException("task_not_found");

        FileContext dataFolder = fileManageService.contextOf(taskId);

        ImportTask importTask = new ImportTask();

        importTask.setTermCode(captureTask.getTermCode());
        importTask.setTermName(importTask.getTermName());
        importTask.setDataPath(dataFolder.getDomainPath());
        importTask.setCaptureTaskId(taskId);
        importTask.setCreateDate(new Date());

        taskExecutor.execute(new CourseDataImportTask(
                this,
                courseInfoService,
                save(importTask),
                caterpillarMonitorStore,
                dataFolder
        ));

        return captureTask;
    }

    @NotNull
    public Optional<ImportTask> findOne(@NotNull String id) {
        return importTaskRepository.findById(id);
    }
}
