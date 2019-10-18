package com.shinonometn.re.ssim.user.service.application.repository

import com.shinonometn.re.ssim.user.service.application.entity.Role
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface RoleRepository : MongoRepository<Role,String>{

    fun findByName(name : String) : Optional<Role>

    fun findAllByName(names : List<String>) : List<Role>
}
