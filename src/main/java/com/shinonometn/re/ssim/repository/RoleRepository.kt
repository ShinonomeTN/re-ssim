package com.shinonometn.re.ssim.repository

import com.shinonometn.re.ssim.models.Role
import org.springframework.data.mongodb.repository.MongoRepository

interface RoleRepository : MongoRepository<Role,String>{

    fun findByName(name : String) : Role?

    fun findAllByName(names : List<String>) : List<Role>
}
