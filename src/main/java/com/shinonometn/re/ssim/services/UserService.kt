package com.shinonometn.re.ssim.services

import com.shinonometn.re.ssim.data.security.User
import com.shinonometn.re.ssim.data.security.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {

    fun saveUser(user : User) = userRepository.save(user)

    fun getUser(id: String): User = userRepository.getById(id)

    fun findAll(pageable: Pageable): Page<User> = userRepository.findAll(pageable)

    fun findUserByName(username: String): Optional<User> = userRepository.findByUsername(username)
}
