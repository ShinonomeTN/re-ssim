package com.shinonometn.re.ssim.service.user.repository

import com.shinonometn.re.ssim.service.user.entity.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface UserRepository : MongoRepository<User, String> {

    fun getById(id: String): User

    @Query("{username : ?0}")
    fun getByUsername(userName: String): User?

    fun findByUsername(username: String): Optional<User>
}
