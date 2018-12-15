package com.shinonometn.re.ssim.application.controller

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.application.controller.froms.LoginForm
import com.shinonometn.re.ssim.application.security.RessimUserPasswordToken
import com.shinonometn.re.ssim.application.security.WebSubjectUtils
import com.shinonometn.re.ssim.commons.validation.Validator
import com.shinonometn.re.ssim.service.user.UserService
import com.shiononometn.commons.web.RexModel
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthenticationAPI(private val userService: UserService,
                        private val validator: Validator) {

    /**
     * User login to system
     */
    @PostMapping(params = ["login"])
    @ApiDescription(title = "User login", description = "Use username and password to login")
    fun login(@RequestBody loginForm: LoginForm,
              @RequestHeader("Host") host: String,
              @RequestHeader("X-Real-IP", defaultValue = "0.0.0.0", required = false) remoteIp: String): RexModel<Any> {

        validator.validate(loginForm)

        val token = RessimUserPasswordToken()
        token.username = loginForm.username
        token.password = loginForm.password!!.toCharArray()
        token.isRememberMe = loginForm.rememberMe
        token.host = host

        WebSubjectUtils.currentSubject().login(token)

        return RexModel(HashMap<String, Any>().apply {
            this["user"] = userService.findByUsername(token.username).orElse(null)
            this["authorizationInfo"] = userService.getUserPermissions(token.username)
        })
    }

    /**
     * User logout from system
     */
    @PostMapping(params = ["logout"])
    @ApiDescription(title = "User logout", description = "User logout")
    fun logout(): RexModel<Any> {
        WebSubjectUtils.currentSubject().logout()
        return RexModel.success()
    }

    /**
     * Current user info
     */
    @GetMapping
    @RequiresAuthentication
    @ApiDescription(title = "Get current user permission info", description = "Get current user permission info")
    fun authorizationInfo() = userService.getUserPermissions(WebSubjectUtils.currentUser().username!!)
}
