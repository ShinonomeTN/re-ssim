package com.shinonometn.re.ssim.service.user.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
class User {
    @Id
    var id: String? = null
    var username: String? = null
    var enable: Boolean = false

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null

    var registerDate: Date? = null
}
