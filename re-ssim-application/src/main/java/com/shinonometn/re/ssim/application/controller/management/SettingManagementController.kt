package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.commons.session.HttpSessionWrapper
import com.shinonometn.re.ssim.application.controller.froms.LoginForm
import com.shinonometn.re.ssim.data.caterpillar.CaterpillarSetting
import com.shinonometn.re.ssim.application.security.AuthorityRequired
import com.shinonometn.re.ssim.application.security.UserDetailsSource
import com.shinonometn.re.ssim.services.LingnanCourseService
import com.shinonometn.re.ssim.services.ManagementService
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/api/mng")
class SettingManagementController(@Autowired private val managementService: com.shinonometn.re.ssim.services.ManagementService,
                                  @Autowired private val lingnanCourseService: com.shinonometn.re.ssim.services.LingnanCourseService,
                                  @Autowired private val userDetailsSource: UserDetailsSource) {

    @PostMapping("/login")
    @ResponseBody
    fun login(@RequestBody loginForm: LoginForm, session: HttpSession) =
            HashMap<String, Any>(2).apply {
                if (!managementService.checkToken(loginForm.username, loginForm.password)) {
                    this["error"] = "login_failed"
                    this["message"] = "unknown_user"
                } else {
                    com.shinonometn.re.ssim.commons.session.HttpSessionWrapper(session).userDetails = userDetailsSource.getUserDetailsByUsername(loginForm.username)
                    this["message"] = "success"
                }
            }

    @PostMapping("/logout")
    @ResponseBody
    fun logout(session: HttpSession) =
            HashMap<String, Any>(1).apply {
                com.shinonometn.re.ssim.commons.session.HttpSessionWrapper(session).userDetails = null
                this["message"] = "success"
            }

    @GetMapping("/settings/user/{id}/profiles")
    @ResponseBody
    @AuthorityRequired(name = "user.caterpillar_settings:get", group = "Caterpillar Settings", description = "List user caterpillar settings.")
    @RequiresPermissions("profile:caterpillar:list")
    fun listUserCaterpillarSettings(@PathVariable("id")id : String): Collection<CaterpillarSetting>? =
            managementService.listSettings(id)

    @PostMapping("/settings/caterpillar",params = ["settingValidate"])
    @ResponseBody
    @AuthorityRequired(name = "user.caterpillar_settings:validate", group = "Caterpillar Settings", description = "Validate a caterpillar setting.")
    @RequiresPermissions("profile:caterpillar:validate")
    fun checkSettings(@RequestBody caterpillarSetting: CaterpillarSetting) =
            HashMap<String,Any>(2).apply {
                if(lingnanCourseService.isSettingValid(caterpillarSetting)){
                    this["message"] = "setting_is_valid"
                }else{
                    this["error"] = "setting_not_valid"
                    this["message"] = "failed_to_login_kingo"
                }
            }
}

