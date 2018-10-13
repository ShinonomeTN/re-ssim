package com.shinonometn.re.ssim.controller.management

import com.shinonometn.re.ssim.commons.session.HttpSessionWrapper
import com.shinonometn.re.ssim.data.security.BaseUserInfoDTO
import com.shinonometn.re.ssim.data.caterpillar.CaterpillarSetting
import com.shinonometn.re.ssim.data.security.User
import com.shinonometn.re.ssim.security.AuthorityRequired
import com.shinonometn.re.ssim.security.UserDetailsSource
import com.shinonometn.re.ssim.services.LingnanCourseService
import com.shinonometn.re.ssim.services.ManagementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/api/mng")
class SettingManagementController(@Autowired private val managementService: ManagementService,
                                  @Autowired private val lingnanCourseService: LingnanCourseService,
                                  @Autowired private val userDetailsSource: UserDetailsSource) {

    @PostMapping("/login")
    @ResponseBody
    fun login(@RequestBody loginForm: LoginForm, session: HttpSession) =
            HashMap<String, Any>(2).apply {
                if (!managementService.checkToken(loginForm.username, loginForm.password)) {
                    this["error"] = "login_failed"
                    this["message"] = "unknown_user"
                } else {
                    HttpSessionWrapper(session).userDetails = userDetailsSource.getUserDetailsByUsername(loginForm.username)
                    this["message"] = "success"
                }
            }

    @PostMapping("/logout")
    @ResponseBody
    fun logout(session: HttpSession) =
            HashMap<String, Any>(1).apply {
                HttpSessionWrapper(session).userDetails = null
                this["message"] = "success"
            }

//    @PutMapping("/settings/user")
//    @ResponseBody
//    @AuthorityRequired(name = "user:create", group = "User management", description = "Create an user.")
//    fun createUser(@RequestBody loginForm: LoginForm) =
//            HashMap<String, Any>(2).apply {
//                if (managementService.getUser(loginForm.username) != null) {
//                    this["error"] = "create_user_failed"
//                    this["message"] = "user_exist"
//                } else {
//                    managementService.saveUser(User().apply {
//                        username = loginForm.username
//                        password = loginForm.password
//                    })
//
//                    this["message"] = "success"
//                }
//            }
//
//    @DeleteMapping("/settings/user/{id}")
//    @ResponseBody
//    @AuthorityRequired(name = "user:delete", group = "User management", description = "Delete an user.")
//    fun deleteUser(@PathVariable("id") id: String) =
//            HashMap<String, Any>(1).apply {
//                managementService.removeUser(id)
//                this["message"] = "success"
//            }

    @PutMapping("/settings/user",params = ["update"])
    @ResponseBody
    @AuthorityRequired(name = "user:update", group = "User management", description = "Update an existed user.")
    fun saveUser(@RequestBody user: User) =
            HashMap<String, Any>(1).apply {
                if (user.id == null) {
                    this["error"] = "save_user_error"
                    this["message"] = "user_not_exist"
                } else {
                    managementService.saveUser(user)
                    this["message"] = "success"
                    this["data"] = user
                }
            }

    @GetMapping("/settings/user")
    @ResponseBody
    @AuthorityRequired(name = "user_list:get", group = "User management", description = "List all user.")
    fun listUser(): List<BaseUserInfoDTO> =
            managementService.listUsers()

    @GetMapping("/settings/user/{id}/profiles")
    @ResponseBody
    @AuthorityRequired(name = "user.caterpillar_settings:get", group = "Caterpillar Settings", description = "List user caterpillar settings.")
    fun listUserCaterpillarSettings(@PathVariable("id")id : String): Collection<CaterpillarSetting>? =
            managementService.listSettings(id)

    @PostMapping("/settings/caterpillar",params = ["settingValidate"])
    @ResponseBody
    @AuthorityRequired(name = "user.caterpillar_settings:validate", group = "Caterpillar Settings", description = "Validate a caterpillar setting.")
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

class LoginForm {
    var username: String? = null
    var password: String? = null
}