package com.shinonometn.re.ssim.service.user

import com.shinonometn.re.ssim.commons.NotFoundException
import com.shinonometn.re.ssim.service.user.entity.Role
import com.shinonometn.re.ssim.service.user.repository.RoleRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleService(private val roleRepository: RoleRepository) {

    fun findByName(name: String): Optional<Role> {
        return roleRepository.findByName(name)
    }

    /**
     *
     * Create a role
     *
     */
    fun create(role: Role) = roleRepository.findByName(role.name!!).orElse(roleRepository.save(role))

    /**
     *
     * Get a role by name
     * throw exception if none
     *
     */
    fun get(name: String): Role = roleRepository.findByName(name).orElseThrow { NotFoundException() }

    fun save(role: Role): Role = roleRepository.save(role)

}
