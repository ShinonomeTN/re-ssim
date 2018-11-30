package com.shinonometn.re.ssim.service.caterpillar.entity;

import com.shinonometn.re.ssim.service.caterpillar.SpiderStatus;
import com.shinonometn.re.ssim.service.caterpillar.commons.CaptureTaskStage;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public interface CaptureTaskDTO {

    String getId();
    String getTermCode();
    String getTermName();
    String getCreateDate();
    Boolean getFinished();
    CaptureTaskStage getStage();

    @Value("#{@lingnanCourseService.getStatusByTask(target)}")
    SpiderStatus getSpiderStatus();

    @Value("#{@lingnanCourseService.getTaskDir(target)}")
    String getTempDir();

    default boolean getFolderExist() {
        return new File(getTempDir()).exists();
    }
}
