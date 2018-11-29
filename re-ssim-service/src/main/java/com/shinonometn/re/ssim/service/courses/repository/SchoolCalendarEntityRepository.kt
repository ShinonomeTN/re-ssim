package com.shinonometn.re.ssim.service.courses.repository

import com.shinonometn.re.ssim.service.courses.entity.SchoolCalendarEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface SchoolCalendarEntityRepository : MongoRepository<SchoolCalendarEntity, String>
