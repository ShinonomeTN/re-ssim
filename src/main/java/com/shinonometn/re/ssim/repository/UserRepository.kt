package com.shinonometn.re.ssim.repository

import com.shinonometn.re.ssim.models.BaseUserInfoDTO
import com.shinonometn.re.ssim.models.User
import com.shinonometn.re.ssim.models.UserDetailsDTO
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface UserRepository : MongoRepository<User, String> {

    @Query("{username : ?0}")
    fun getByUsername(userName: String) : User?

    @Query("{}")
    fun findAllDto() : List<BaseUserInfoDTO>

    @Query("{username : ?0}")
    fun findDTOByName(name : String) : UserDetailsDTO
}
