package com.shinonometn.re.ssim.user.service.application.service

import com.shinonometn.re.ssim.user.service.application.entity.Permission
import com.shinonometn.re.ssim.user.service.application.entity.User
import com.shinonometn.re.ssim.user.service.application.entity.UserPermissionInfo
import com.shinonometn.re.ssim.user.service.application.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashSet

@Service
class UserService(private val userRepository: UserRepository,
                  private val roleService: RoleService,
                  private val permissionService: PermissionService) {

    fun save(user: User): User = userRepository.save(user)

    fun get(id: String): User = userRepository.getById(id)

    fun findAll(pageable: Pageable): Page<User> = userRepository.findAll(pageable)

    fun findByUsername(username: String): Optional<User> = userRepository.findByUsername(username)

    fun getUserPermissions(username: String): UserPermissionInfo {
        val result = UserPermissionInfo()

        val permission = permissionService.findByUser(username).orElse(Permission())
        val stringPermissions = HashSet<String>()
                .apply { addAll(permission.extraPermissions) }
                .apply {
                    addAll(permission
                            .roles
                            .map(roleService::findByName)
                            .filter { it.isPresent }
                            .flatMap { it.get().permissions })
                }

        result.user = findByUsername(username).orElse(null)
        result.permissions = stringPermissions
        result.roles = permission.roles

        return result
    }

    fun hasUser(): Boolean = userRepository.count() > 0
}
