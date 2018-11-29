package com.shinonometn.re.ssim.service.user.repository

import com.shinonometn.re.ssim.service.user.entity.Permission
import org.springframework.data.mongodb.repository.MongoRepository

interface PermissionRepository : MongoRepository<Permission, String> {

    fun findAllByUsername(username: String): MutableList<Permission>
}