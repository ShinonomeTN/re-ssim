package com.shinonometn.re.ssim.user.service.application.entity

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

@Document
class User : Serializable {
    @Id
    var id: String? = null
    var username: String? = null
    var nickname: String? = null
    var avatar: String? = null
    var enable: Boolean = false

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null

    var registerDate: Date? = null
}
