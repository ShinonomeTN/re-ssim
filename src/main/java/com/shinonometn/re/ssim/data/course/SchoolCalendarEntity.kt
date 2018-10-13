package com.shinonometn.re.ssim.data.course

import com.shinonometn.re.ssim.commons.SchoolCalendar
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.beans.Transient
import java.time.ZoneId
import java.util.*

@Document
class SchoolCalendarEntity {

    @Id
    var id: String? = null

    var term: String? = null
    var startDate: Date? = null
    var endDate: Date? = null
    var createTime: Date? = null

    @Transient
    fun fromSchoolCalendar(schoolCalendar: SchoolCalendar) {
        this.term = schoolCalendar.name
        this.startDate = Date.from(schoolCalendar.startDate.atZone(ZoneId.systemDefault()).toInstant())
        this.endDate = Date.from(schoolCalendar.endDate.atZone(ZoneId.systemDefault()).toInstant())
    }
}
