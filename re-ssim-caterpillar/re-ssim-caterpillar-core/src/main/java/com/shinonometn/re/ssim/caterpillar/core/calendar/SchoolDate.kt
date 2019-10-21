package com.shinonometn.re.ssim.caterpillar.core.calendar

import com.fasterxml.jackson.annotation.JsonFormat

import java.time.DayOfWeek
import java.util.Objects

class SchoolDate {

    // Schools have their terms
    var term: String? = null
    var week: Int = 0
    @get:JsonFormat(shape = JsonFormat.Shape.NUMBER)
    var day: DayOfWeek? = null

    constructor() {}

    constructor(term: String, week: Int, day: DayOfWeek) {
        this.term = term
        this.week = week
        this.day = day
    }

    override fun toString(): String {
        return "SchoolDate{" +
                "termName='" + term + '\''.toString() +
                ", week=" + week +
                ", day=" + day +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as SchoolDate?
        return week == that!!.week &&
                term == that.term &&
                day == that.day
    }

    override fun hashCode(): Int {
        return Objects.hash(term, week, day)
    }
}
