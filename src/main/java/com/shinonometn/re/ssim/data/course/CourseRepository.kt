package com.shinonometn.re.ssim.data.course

import org.springframework.data.mongodb.repository.MongoRepository

interface CourseRepository : MongoRepository<CourseEntity, String>
