package com.shinonometn.re.ssim.application.configuration.preparation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
interface ServerInitializeTask : Runnable {

    @JsonProperty("name")
    fun name(): String = this::class.simpleName!!

    @JsonProperty("order")
    fun order(): Int

    @JsonProperty("onlyRunAtFirstTime")
    fun onlyAtFirstTime(): Boolean
}