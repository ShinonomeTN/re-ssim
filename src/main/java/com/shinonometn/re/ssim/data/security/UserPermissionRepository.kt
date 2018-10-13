package com.shinonometn.re.ssim.data.security

import org.springframework.data.mongodb.repository.MongoRepository

interface UserPermissionRepository : MongoRepository<UserPermission, String> {
    fun findByUserId(userId : String) : UserPermission?
}
