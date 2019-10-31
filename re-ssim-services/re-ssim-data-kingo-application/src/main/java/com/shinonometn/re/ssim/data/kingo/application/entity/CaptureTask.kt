package com.shinonometn.re.ssim.data.kingo.application.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.data.kingo.application.base.CaptureTaskStage
import com.shinonometn.re.ssim.data.kingo.application.utils.JsonMapAttributeConverter
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "tb_caterpillar_tasks")
@JsonInclude(JsonInclude.Include.NON_NULL)
class CaptureTask : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    var versionCode: String? = null
    var termCode: String? = null
    var termName: String? = null

    @Convert(converter = JsonMapAttributeConverter::class)
    @Column(columnDefinition = "TEXT")
    var captureProfile: MutableMap<String, Any?>? = null

    @Temporal(TemporalType.TIMESTAMP)
    var createDate: Date? = null

    var stage: CaptureTaskStage? = null

    var stageReport: String = ""

}
