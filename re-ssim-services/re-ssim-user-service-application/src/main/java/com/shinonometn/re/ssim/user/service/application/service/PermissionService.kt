package com.shinonometn.re.ssim.user.service.application.service

import com.shinonometn.re.ssim.user.service.application.entity.Permission
import com.shinonometn.re.ssim.user.service.application.repository.PermissionRepository
import com.shinonometn.re.ssim.user.service.application.repository.RoleRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PermissionService(private val permissionRepository: PermissionRepository,
                        private val roleRepository: RoleRepository) {

    fun findByUser(username: String): Optional<Permission> = permissionRepository.findByUser(username)

    fun save(permission: Permission): Permission = permissionRepository.save(permission.apply {
        updateDate = Date()
    })
}
