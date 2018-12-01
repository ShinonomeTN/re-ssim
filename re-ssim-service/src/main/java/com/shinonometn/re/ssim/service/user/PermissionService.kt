package com.shinonometn.re.ssim.service.user

import com.shinonometn.re.ssim.service.user.entity.Permission
import com.shinonometn.re.ssim.service.user.repository.PermissionRepository
import com.shinonometn.re.ssim.service.user.repository.RoleRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PermissionService(private val permissionRepository: PermissionRepository,
                        private val roleRepository: RoleRepository) {

    fun findByUser(username: String): Optional<Permission> = permissionRepository.findByUsername(username)

    fun save(permission: Permission): Permission = permissionRepository.save(permission.apply {
        updateDate = Date()
    })
}
