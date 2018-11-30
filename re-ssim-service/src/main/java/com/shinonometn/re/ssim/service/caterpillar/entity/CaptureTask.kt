package com.shinonometn.re.ssim.service.caterpillar.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import com.shinonometn.re.ssim.service.caterpillar.commons.CaptureTaskStage

import java.util.*

@Document
class CaptureTask {

    @Id
    var id: String? = null
    var termCode: String? = null
    var termName: String? = null
    var createDate: Date? = null
    var stage: CaptureTaskStage? = null
    var finished: Boolean = false

}
