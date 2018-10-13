package com.shinonometn.re.ssim.data.caterpillar;

import com.shinonometn.re.ssim.caterpillar.SpiderStatus;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public interface CaptureTaskDTO {

    String getId();
    String getTermCode();
    String getTermName();
    String getCreateDate();
    Boolean getFinished();
    String getStage();

    @Value("#{@lingnanCourseService.getStatusByTask(target)}")
    SpiderStatus getSpiderStatus();

    @Value("#{@lingnanCourseService.getTaskDir(target)}")
    String getTempDir();

    default boolean getFolderExist() {
        return new File(getTempDir()).exists();
    }
}
