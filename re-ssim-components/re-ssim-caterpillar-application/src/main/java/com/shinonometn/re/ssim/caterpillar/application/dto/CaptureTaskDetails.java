package com.shinonometn.re.ssim.caterpillar.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTask;
import com.shinonometn.re.ssim.service.caterpillar.SpiderStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaptureTaskDetails {
    private CaptureTask taskInfo;
    private SpiderStatus runningTaskStatus;

    public CaptureTask getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(CaptureTask taskInfo) {
        this.taskInfo = taskInfo;
    }

    public SpiderStatus getRunningTaskStatus() {
        return runningTaskStatus;
    }

    public void setRunningTaskStatus(SpiderStatus runningTaskStatus) {
        this.runningTaskStatus = runningTaskStatus;
    }
}
