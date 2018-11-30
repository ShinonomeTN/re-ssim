package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.service.user.UserService
import com.shinonometn.re.ssim.service.user.entity.User
import com.shiononometn.commons.web.RexModel
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.beans.BeanUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    /**
     *
     * Create a user
     *
     */
    @PutMapping
    @ApiDescription(title = "Create a user", description = "Create a User")
    @RequiresPermissions("user:write")
    fun create(@RequestBody user: User): Any {
        userService.findByUsername(user.username!!).ifPresent { throw BusinessException("user_exists") }
        return userService.save(user)
    }

    /**
     *
     * Disable a user
     *
     */
    @PostMapping("/{username}", params = ["disable"])
    @ApiDescription(title = "Disable a user", description = "Disable a user")
    @RequiresPermissions("user:delete")
    fun disable(@PathVariable("username") username: String, @RequestParam("disable") disable: Boolean): User = userService
            .save(userService.findByUsername(username)
                    .orElseThrow { BusinessException("user_not_exists") }
                    .apply { enable = !disable })

    /**
     *
     * List all users
     *
     */
    @GetMapping
    @ApiDescription(title = "List all user", description = "List all user")
    @RequiresPermissions("user:list")
    fun list(@PageableDefault pageable: Pageable): Page<User> = userService.findAll(pageable)

    /**
     *
     * Get an user
     *
     */
    @GetMapping("/{name}")
    @ApiDescription(title = "Get a user", description = "Get a user")
    @RequiresPermissions("user:read")
    fun get(@PathVariable("name") username: String): User = userService
            .findByUsername(username)
            .orElseThrow { BusinessException("user_not_exists") }

    @PostMapping("/{username}")
    @ApiDescription(title = "Edit a user", description = "Edit a user")
    @RequiresPermissions("user:write")
    fun edit(@PathVariable("username") username: String, @RequestBody user: User): User = userService.save(userService
            .findByUsername(username)
            .orElseThrow { BusinessException("user_not_found") }
            .apply { BeanUtils.copyProperties(user, this, "id", "password", "registerDate") })

    /**
     *
     * Update user password
     *
     */
    @PostMapping("/{username}/password")
    @ApiDescription(title = "Change user password", description = "Change user password")
    @RequiresPermissions("user:write")
    fun updatePassword(@PathVariable("username") username: String, @RequestBody newPassword: String): RexModel<Any> {

        userService.save(userService
                .findByUsername(username)
                .orElseThrow { BusinessException("user_not_exists") }
                .apply { password = newPassword })

        return RexModel.success()
    }
}
