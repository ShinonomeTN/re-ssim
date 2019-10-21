package com.shinonometn.re.ssim.data.kingo.application.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskCreateRequest(
        var termCode: String? = null,
        var profileId: Int? = null
)