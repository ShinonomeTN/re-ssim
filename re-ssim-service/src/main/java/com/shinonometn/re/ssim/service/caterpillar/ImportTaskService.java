package com.shinonometn.re.ssim.service.caterpillar;

import com.shinonometn.re.ssim.commons.BusinessException;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.commons.JSON;
import com.shinonometn.re.ssim.commons.file.fundation.FileContext;
import com.shinonometn.re.ssim.service.caterpillar.commons.CaptureTaskStage;
import com.shinonometn.re.ssim.service.caterpillar.entity.CaptureTask;
import com.shinonometn.re.ssim.service.caterpillar.entity.ImportTask;
import com.shinonometn.re.ssim.service.caterpillar.repository.CaptureTaskRepository;
import com.shinonometn.re.ssim.service.caterpillar.repository.ImportTaskRepository;
import com.shinonometn.re.ssim.service.courses.entity.CourseEntity;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class ImportTaskService {

    private final CaptureTaskRepository captureTaskRepository;
    private final ImportTaskRepository importTaskRepository;
    private final CaterpillarFileManageService fileManageService;

    public ImportTaskService(CaptureTaskRepository captureTaskRepository,
                             ImportTaskRepository importTaskRepository,
                             CaterpillarFileManageService fileManageService) {

        this.captureTaskRepository = captureTaskRepository;
        this.importTaskRepository = importTaskRepository;
        this.fileManageService = fileManageService;
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
    public CaptureTask startImport(String taskId) {

        CaptureTask captureTask = captureTaskRepository.findById(taskId).orElse(null);
        if (captureTask == null) throw new BusinessException("task_not_found");

        FileContext dataFolder = fileManageService.contextOf(taskId);

        ImportTask importTask = new ImportTask();

        importTask.setTermCode(captureTask.getTermCode());
        importTask.setTermName(importTask.getTermName());

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

}
