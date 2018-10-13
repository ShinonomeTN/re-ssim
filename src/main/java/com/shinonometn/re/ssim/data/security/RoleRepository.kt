package com.shinonometn.re.ssim.data.security

import org.springframework.data.mongodb.repository.MongoRepository

interface RoleRepository : MongoRepository<Role,String>{

    fun findByName(name : String) : Role?

    fun findAllByName(names : List<String>) : List<Role>
}
