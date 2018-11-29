package com.shinonometn.re.ssim.service.user

import com.shinonometn.re.ssim.service.user.entity.User
import com.shinonometn.re.ssim.service.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {

    fun save(user: User) = userRepository.save(user)

    fun get(id: String): User = userRepository.getById(id)

    fun findAll(pageable: Pageable): Page<User> = userRepository.findAll(pageable)

    fun findByUsername(username: String): Optional<User> = userRepository.findByUsername(username)

    fun hasUser() : Boolean = userRepository.count() > 0
}
