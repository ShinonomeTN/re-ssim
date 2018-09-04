package com.shinonometn.re.ssim.repository

import com.shinonometn.re.ssim.models.AttributePermission
import org.springframework.data.mongodb.repository.DeleteQuery
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface AttributePermissionRepository : MongoRepository<AttributePermission, String> {

    @DeleteQuery("{ 'scanTime' : { \$lt : ?0 } }")
    fun deleteAllOldItems(deadline: Date)
}
