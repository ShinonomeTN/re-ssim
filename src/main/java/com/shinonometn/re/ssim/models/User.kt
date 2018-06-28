package com.shinonometn.re.ssim.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class User {
    @Id
    var id: String? = null
    var username: String? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null

    var caterpillarSettings : MutableSet<CaterpillarSettings>? = null
}
