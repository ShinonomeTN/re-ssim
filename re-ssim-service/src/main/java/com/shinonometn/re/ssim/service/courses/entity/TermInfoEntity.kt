package com.shinonometn.re.ssim.service.courses.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

@Document
class TermInfoEntity : Serializable {


    constructor(code: String?, name: String?) {
        this.code = code
        this.name = name
    }

    constructor()

    @Id
    var id: String? = null

    var code: String? = null
    var name: String? = null

    var courseCount: Int = 0
    var courseTypes: MutableList<String>? = null

    var minWeek: Int? = null
    var maxWeek: Int? = null

    var dataVersion: String? = null

    var updateDate: Date? = null
}
