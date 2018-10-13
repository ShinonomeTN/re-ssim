package com.shinonometn.re.ssim.data.security

import org.springframework.data.mongodb.repository.DeleteQuery
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface AttributePermissionRepository : MongoRepository<AttributePermission, String> {

    @DeleteQuery("{ 'scanTime' : { \$lt : ?0 } }")
    fun deleteAllOldItems(deadline: Date)

    fun findByMethodSign(methodSign: String?): AttributePermission?
}
