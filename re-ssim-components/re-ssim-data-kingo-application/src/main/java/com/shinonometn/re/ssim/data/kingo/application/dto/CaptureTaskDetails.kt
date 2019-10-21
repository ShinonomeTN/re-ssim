package com.shinonometn.re.ssim.data.kingo.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.entity.ImportTask
import com.shinonometn.re.ssim.service.caterpillar.SpiderStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
class CaptureTaskDetails {

    var taskInfo: CaptureTask? = null
    var capturing: SpiderStatus? = null
    var bundling: TaskBundleInfo? = null
    var importing: ImportTask? = null

    data class TaskBundleInfo(var hasBundleFile: Boolean = false)
}
