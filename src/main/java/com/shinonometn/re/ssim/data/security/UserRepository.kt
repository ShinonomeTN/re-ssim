package com.shinonometn.re.ssim.data.security

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface UserRepository : MongoRepository<User, String> {

    fun getById(id : String) : User

    @Query("{username : ?0}")
    fun getByUsername(userName: String) : User?

    @Query("{}")
    fun findAllDto() : List<BaseUserInfoDTO>

    fun findByUsername(username: String): Optional<User>
}
