package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.application.controller.froms.CreateUserValidator
import com.shinonometn.re.ssim.data.security.User
import com.shinonometn.re.ssim.application.security.AuthorityRequired
import com.shinonometn.re.ssim.services.UserService
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    /**
     *
     * Create a user
     *
     */
    @PutMapping
    @AuthorityRequired(name = "user:save", description = "Create a User", group = "user_management")
    @RequiresPermissions("user:write")
    fun createUser(@Validated(CreateUserValidator::class) @RequestBody user: User): Any {

        if (userService.findUserByName(user.username!!).isPresent) throw com.shinonometn.re.ssim.commons.BusinessException("user_existed")

        return userService.saveUser(User().apply {
            username = user.username
            password = user.password

            registerDate = Date()
            latestUpdateDate = registerDate

        })
    }

    /**
     *
     * Delete a user
     *
     */
    @DeleteMapping("/{id}")
    @AuthorityRequired(name = "user:delete", description = "Delete a user", group = "user_management")
    @RequiresPermissions("user:delete")
    fun deleteUser(@PathVariable("id") id: String): Any {
        val user = userService.getUser(id)

        return userService.saveUser(user.apply {
            enable = true
        })
    }

    /**
     *
     * List all users
     *
     */
    @GetMapping
    @AuthorityRequired(name = "user_list:get", description = "List all user", group = "user_management")
    @RequiresPermissions("user:list")
    fun listUsers(@PageableDefault pageable: Pageable): Page<User> =
            userService.findAll(pageable)

    /**
     *
     * Get an user
     *
     */
    @GetMapping("/{id}")
    @AuthorityRequired(name = "user:get", description = "Get one user", group = "user_management")
    @RequiresPermissions("user:read")
    fun getUser(@PathVariable("id") id: String) =
            userService.getUser(id)

    /**
     *
     * Update user password
     *
     */
    @PostMapping("/{id}", params = ["updatePassword"])
    @AuthorityRequired(name = "user.password:save", description = "Change user password", group = "user_management")
    @RequiresPermissions("user:write")
    fun updateUserPassword(@PathVariable("id") id: String, @RequestBody newPassword: String) : Any {
        val user = userService.getUser(id)

        userService.saveUser(user.apply {
            password = newPassword
        })

        return true
    }
}
