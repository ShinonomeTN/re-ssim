package com.shinonometn.re.ssim.service.courses.repository

import com.shinonometn.re.ssim.service.courses.entity.SchoolCalendarEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface SchoolCalendarEntityRepository : MongoRepository<SchoolCalendarEntity, String> {
    fun findByTerm(termCode: String): Optional<SchoolCalendarEntity>
}
