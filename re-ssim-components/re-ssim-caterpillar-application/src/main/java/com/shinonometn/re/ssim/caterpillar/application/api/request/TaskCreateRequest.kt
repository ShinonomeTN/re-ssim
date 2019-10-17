package com.shinonometn.re.ssim.caterpillar.application.api.request

import java.util.*

data class TaskCreateRequest(private val map: MutableMap<String, Any> = HashMap(2)) {
    var termCode: String by map
    var schoolIdentity: String by map
}