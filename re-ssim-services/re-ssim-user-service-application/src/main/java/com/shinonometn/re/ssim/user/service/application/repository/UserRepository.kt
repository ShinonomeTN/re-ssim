package com.shinonometn.re.ssim.user.service.application.repository

import com.shinonometn.re.ssim.user.service.application.entity.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : MongoRepository<User, String> {

    fun getById(id: String): User

    @Query("{username : ?0}")
    fun getByUsername(userName: String): User?

    fun findByUsername(username: String): Optional<User>
}
