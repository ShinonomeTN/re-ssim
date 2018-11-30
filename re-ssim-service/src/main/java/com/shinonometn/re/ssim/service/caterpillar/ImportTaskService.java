package com.shinonometn.re.ssim.service.caterpillar;

import com.shinonometn.re.ssim.commons.BusinessException;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.commons.file.fundation.FileContext;
import com.shinonometn.re.ssim.service.caterpillar.plugin.CaterpillarMonitorStore;
import com.shinonometn.re.ssim.service.caterpillar.entity.CaptureTask;
import com.shinonometn.re.ssim.service.caterpillar.entity.ImportTask;
import com.shinonometn.re.ssim.service.caterpillar.repository.CaptureTaskRepository;
import com.shinonometn.re.ssim.service.caterpillar.repository.ImportTaskRepository;
import com.shinonometn.re.ssim.service.caterpillar.task.CourseDataImportTask;
import com.shinonometn.re.ssim.service.courses.CourseInfoService;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ImportTaskService {

    private final CaptureTaskRepository captureTaskRepository;
    private final ImportTaskRepository importTaskRepository;

    private final CaterpillarMonitorStore caterpillarMonitorStore;
    private final CaterpillarFileManageService fileManageService;

    private final CourseInfoService courseInfoService;

    private final TaskExecutor taskExecutor;

    public ImportTaskService(CaptureTaskRepository captureTaskRepository,
                             ImportTaskRepository importTaskRepository,
                             CaterpillarFileManageService fileManageService,
                             CaterpillarMonitorStore caterpillarMonitorStore,
                             CourseInfoService courseInfoService, TaskExecutor taskExecutor) {

        this.captureTaskRepository = captureTaskRepository;
        this.importTaskRepository = importTaskRepository;
        this.fileManageService = fileManageService;
        this.caterpillarMonitorStore = caterpillarMonitorStore;
        this.courseInfoService = courseInfoService;
        this.taskExecutor = taskExecutor;
    }

    public ImportTask save(ImportTask importTask){
        return importTaskRepository.save(importTask);
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

}
