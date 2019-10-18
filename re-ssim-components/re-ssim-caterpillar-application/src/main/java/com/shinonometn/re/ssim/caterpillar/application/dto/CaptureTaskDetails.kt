package com.shinonometn.re.ssim.caterpillar.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.caterpillar.application.entity.CaptureTask
import com.shinonometn.re.ssim.service.caterpillar.SpiderStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
class CaptureTaskDetails {

    var task: CaptureTask? = null
    var status: SpiderStatus? = null
    var bundle: TaskBundleInfo? = null

    data class TaskBundleInfo(var hasBundleFile: Boolean = false, var bundleFileStatus: String = "")
}
