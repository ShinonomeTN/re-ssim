package com.shinonometn.re.ssim.services

import com.shinonometn.re.ssim.data.security.Role
import com.shinonometn.re.ssim.data.security.User
import com.shinonometn.re.ssim.data.security.UserPermission
import com.shinonometn.re.ssim.data.security.RoleRepository
import com.shinonometn.re.ssim.data.security.UserPermissionRepository
import org.springframework.stereotype.Service

@Service
class PermissionService(private val userPermissionRepository: UserPermissionRepository,
                        private val roleRepository: RoleRepository) {

    /**
     *
     * Create a role or return the existed one
     *
     */
    fun createRole(name: String) : Role {
        // If role existed return the existed one
        roleRepository.findByName(name)?.let { return it }

        return roleRepository.save(Role().apply {
            this.name = name
        })
    }

    /**
     *
     * Get a role by name
     * throw exception if none
     *
     */
    fun getRole(name: String) : Role = roleRepository.findByName(name)!!

    /**
     *
     * Save modifications of a role
     *
     */
    fun saveRole(role: Role) {
        roleRepository.save(role)
    }

    /**
     *
     * Grant a role to user
     *
     */
    fun grantUserRole(role: Role, user: User) {
        val userPermission = userPermissionRepository.findByUserId(user.id!!) ?: UserPermission().apply {
            userId = user.id
        }

        userPermissionRepository.save(userPermission.apply {
            roles.plus(role.name)
        })
    }

    /**
     *
     * Revoke a role from user
     *
     */
    fun revokeUserRole(role: Role, user: User) {
        val userPermission = userPermissionRepository.findByUserId(user.id!!) ?: return

        userPermissionRepository.save(userPermission.apply {
            roles.minus(role)
        })
    }
}
