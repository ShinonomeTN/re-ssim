package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.models.BaseUserInfoDTO
import com.shinonometn.re.ssim.models.CaterpillarSettings
import com.shinonometn.re.ssim.models.User
import com.shinonometn.re.ssim.services.LingnanCourseService
import com.shinonometn.re.ssim.services.SettingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/api/mng")
class SettingManagementController(@Autowired private val settingService: SettingService,
                                  @Autowired private val lingnanCourseService: LingnanCourseService) {

    @PostMapping("/login")
    @ResponseBody
    fun login(@RequestBody loginForm: LoginForm, session: HttpSession) =
            HashMap<String, Any>(2).apply {
                if (!settingService.checkToken(loginForm.username, loginForm.password)) {
                    this["error"] = "login_failed"
                    this["message"] = "unknown_user"
                } else {
                    session.setAttribute("loginUsername", loginForm.username)
                    this["message"] = "success"
                }
            }

    @PostMapping("/logout")
    @ResponseBody
    fun logout(session: HttpSession) =
            HashMap<String, Any>(1).apply {
                session.removeAttribute("loginUsername")
                this["message"] = "success"
            }

    @PutMapping("/settings/user")
    @ResponseBody
    fun createUser(@RequestBody loginForm: LoginForm) =
            HashMap<String, Any>(2).apply {
                if (settingService.getUser(loginForm.username) != null) {
                    this["error"] = "create_user_failed"
                    this["message"] = "user_exist"
                } else {
                    settingService.saveUser(User().apply {
                        username = loginForm.username
                        password = loginForm.password
                    })

                    this["message"] = "success"
                }
            }

    @DeleteMapping("/settings/user/{id}")
    @ResponseBody
    fun deleteUser(@PathVariable("id") id: String) =
            HashMap<String, Any>(1).apply {
                settingService.removeUser(id)
                this["message"] = "success"
            }

    @PutMapping("/settings/user",params = ["update"])
    @ResponseBody
    fun saveUser(@RequestBody user: User) =
            HashMap<String, Any>(1).apply {
                if (user.id == null) {
                    this["error"] = "save_user_error"
                    this["message"] = "user_not_exist"
                } else {
                    settingService.saveUser(user)
                    this["message"] = "success"
                    this["data"] = user
                }
            }

    @GetMapping("/settings/user")
    @ResponseBody
    fun listUser(): List<BaseUserInfoDTO> =
            settingService.listUsers()

    @GetMapping("/settings/user/{id}/profiles")
    @ResponseBody
    fun listSettings(@PathVariable("id")id : String): Set<CaterpillarSettings>? =
            settingService.listSettings(id)

    @PostMapping("/settings/caterpillar",params = ["settingValidate"])
    @ResponseBody
    fun checkSettings(@RequestBody caterpillarSettings: CaterpillarSettings) =
            HashMap<String,Any>(2).apply {
                if(lingnanCourseService.isSettingVaild(caterpillarSettings)){
                    this["message"] = "setting_is_valid"
                }else{
                    this["error"] = "setting_not_valid"
                    this["message"] = "failed_to_login_kingo"
                }
            }
}

class LoginForm {
    var username: String? = null
    var password: String? = null
}