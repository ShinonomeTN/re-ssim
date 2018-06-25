package com.shinonometn.re.ssim.repository

import com.shinonometn.re.ssim.models.CourseEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface CourseRepository : MongoRepository<CourseEntity, String>
