package com.shinonometn.re.ssim.service.user.repository

import com.shinonometn.re.ssim.service.user.entity.Permission
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface PermissionRepository : MongoRepository<Permission, String> {

    fun findByUser(username: String): Optional<Permission>
}