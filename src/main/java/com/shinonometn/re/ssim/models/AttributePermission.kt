package com.shinonometn.re.ssim.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

@Document
class AttributePermission : Serializable {

    @Id
    var id: String? = null

    var identity: String? = null
    var methodSign: String? = null

    var group: String? = null
    var description: String? = null

    var scanTime: Date? = null
}











