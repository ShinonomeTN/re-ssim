package com.shinonometn.re.ssim.data.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class User {
    @Id
    var id: String? = null
    var username: String? = null
    var deleted: Boolean = false

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null

    var registerDate : Date? = null
    var latestUpdateDate : Date? = null

    var roles: MutableList<String>? = null
}
