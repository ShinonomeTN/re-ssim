package com.shinonometn.re.ssim.caterpillar.application.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.caterpillar.application.commons.CaptureTaskStage
import com.shinonometn.re.ssim.caterpillar.application.utils.JsonMapAttributeConverter
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Convert

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
class CaptureTask : Serializable {

    @Id
    var id: Int? = null

    var schoolIdentity: String? = null
    var termCode: String? = null
    var termName: String? = null

    @Convert(converter = JsonMapAttributeConverter::class)
    @Column(columnDefinition = "TEXT")
    var captureProfile: MutableMap<String, Any?>? = null

    var createDate: Date? = null

    var stage: CaptureTaskStage? = null

    var stageReport: String = ""

}
