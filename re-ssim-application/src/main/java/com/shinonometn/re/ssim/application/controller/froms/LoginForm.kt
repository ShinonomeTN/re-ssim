package com.shinonometn.re.ssim.application.controller.froms

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class LoginForm {
    var username: String? = null
    var password: String? = null
    var rememberMe : Boolean = false
}