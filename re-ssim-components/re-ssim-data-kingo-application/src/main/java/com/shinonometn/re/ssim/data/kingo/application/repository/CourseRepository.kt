package com.shinonometn.re.ssim.data.kingo.application.repository

import com.shinonometn.re.ssim.data.kingo.application.entity.CourseEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface CourseRepository : MongoRepository<CourseEntity, String> {
}
