package com.shinonometn.re.ssim.user.service.application.repository

import com.shinonometn.re.ssim.user.service.application.entity.Permission
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface PermissionRepository : MongoRepository<Permission, String> {

    fun findByUser(username: String): Optional<Permission>
}