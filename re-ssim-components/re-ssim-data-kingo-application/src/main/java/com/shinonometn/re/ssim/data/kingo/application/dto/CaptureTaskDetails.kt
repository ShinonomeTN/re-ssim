package com.shinonometn.re.ssim.data.kingo.application.dto

import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.entity.ImportTask
import com.shinonometn.re.ssim.service.caterpillar.SpiderStatus

class CaptureTaskDetails {

    var taskInfo: CaptureTask? = null
    var taskThread: SpiderStatus? = null
    var bundling: TaskBundleInfo? = null
    var importing: ImportTask? = null

}
