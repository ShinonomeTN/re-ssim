package com.shinonometn.re.ssim.service.user.repository

import com.shinonometn.re.ssim.service.user.entity.Role
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface RoleRepository : MongoRepository<Role,String>{

    fun findByName(name : String) : Optional<Role>

    fun findAllByName(names : List<String>) : List<Role>
}
