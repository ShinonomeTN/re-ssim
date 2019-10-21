package com.shinonometn.re.ssim.data.kingo.application.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskCreateRequest(private val map: MutableMap<String, Any> = HashMap(2)) {
    var termCode: String by map
}