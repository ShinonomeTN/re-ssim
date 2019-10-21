package com.shinonometn.re.ssim.caterpillar.core.calendar

import java.time.LocalDateTime

interface SchoolCalendar {

    val name: String
    val startDate: LocalDateTime
    val endDate: LocalDateTime
    val daysOfTerm: Int

    fun getFromDateTime(dateTime: LocalDateTime): SchoolDate
}
