package com.shinonometn.re.ssim.models;

import com.shinonometn.re.ssim.caterpillar.SpiderStatus;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface CaptureTaskDTO{

    String getId();
    String getTermCode();
    String getTermName();
    Date getCreateDate();

    @Value("#{@lingnanCourseService.getStatusByTask(target)}")
    SpiderStatus getSpiderStatus();
}
