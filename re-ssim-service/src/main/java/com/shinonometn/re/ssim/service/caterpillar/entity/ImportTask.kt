package com.shinonometn.re.ssim.service.caterpillar.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.service.caterpillar.commons.ImportTaskStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
class ImportTask {

    @Id
    var id : String? = null

    var dataPath : String? = null

    var status : ImportTaskStatus = ImportTaskStatus.NONE
    var statusReport: String = ""

    var termCode : String? = null
    var termName : String? = null

    var createDate : Date? = null
    var finishDate : Date? = null
}
