package com.shinonometn.re.ssim.service.courses.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class TermInfoEntity {
    @Id
    var id: String? = null

    var code : String? = null
    var name : String? = null

    var updateDate : Date? = null
}
