package com.shinonometn.re.ssim.service.calendar

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface SchoolCalendarEntityRepository : MongoRepository<SchoolCalendarEntity, String> {
    fun findByTerm(termCode: String): Optional<SchoolCalendarEntity>

    fun findByTermName(name : String): Optional<SchoolCalendarEntity>

    fun existsByTermName(termName: String) : Boolean
}
