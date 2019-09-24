package com.shinonometn.re.ssim.service.data

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
class ImportTask: Serializable {

    @Id
    var id : String? = null

    var captureTaskId : String? = null

    var dataPath : String? = null

    var status : ImportTaskStatus = ImportTaskStatus.NONE
    var statusReport: String = ""

    var termCode : String? = null
    var termName : String? = null

    var createDate : Date? = null
    var finishDate : Date? = null
}
