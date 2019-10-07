package com.shinonometn.re.ssim.caterpillar.application.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.caterpillar.application.commons.CaptureTaskStage
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
class CaptureTask : Serializable {

    @Id
    var id: String? = null
    var termCode: String? = null
    var termName: String? = null
    var createDate: Date? = null

    var stage: CaptureTaskStage? = null
    var stageReport: String = ""


}
