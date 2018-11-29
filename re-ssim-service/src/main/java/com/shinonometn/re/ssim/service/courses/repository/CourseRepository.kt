package com.shinonometn.re.ssim.service.courses.repository

import com.shinonometn.re.ssim.service.courses.entity.CourseEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface CourseRepository : MongoRepository<CourseEntity, String>
