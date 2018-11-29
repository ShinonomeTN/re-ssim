package com.shinonometn.re.ssim.service.user

import com.shinonometn.re.ssim.service.user.entity.Role
import com.shinonometn.re.ssim.service.user.repository.RoleRepository
import org.springframework.stereotype.Service

import java.util.Optional

@Service
class RoleService(private val roleRepository: RoleRepository) {

    fun findByName(name: String): Optional<Role> {
        return roleRepository.findByName(name)
    }

    fun save(role: Role): Role = roleRepository.save(role)
}
