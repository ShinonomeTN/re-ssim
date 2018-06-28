package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.services.CourseInfoService
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/api/term/{term}")
open class CourseQueryController
constructor(@Autowired private val courseInfoService: CourseInfoService) {

    @GetMapping("/course", params = ["class", "week"])
    open fun queryClassWeekCourses(@PathVariable("term") term: String,
                                   @RequestParam("class", required = true) clazz: String,
                                   @RequestParam("week", required = true) week: Int,
                                   @RequestParam("excludeType", required = false) excludeType: List<String>?): Any {

        return courseInfoService.executeAggregation(newAggregation(
                // Project necessary fields
                project("term",
                        "code",
                        "name",
                        "lessons.teacher",
                        "lessons.classAttend",
                        "lessons.classType",
                        "lessons.timePoint",
                        "lessons.position"),

                // Find matched lesson
                match(where("term").`is`(term)
                        .and("lessons.classAttend").`in`(clazz).also {

                    // If exclude list specified
                    if (excludeType != null)
                        it.and("lessons.classType").nin(excludeType)
                }),

                // Explain records
                unwind("lessons"),
                unwind("lessons.timePoint"),

                // Find lessons matched given week
                match(where("lessons.timePoint.week").`is`(week)),

                // Group up by timePoint
                group("lessons.timePoint")
                        // Here use raw query
                        .addToSet(Document().apply {
                            put("code", "\$code")
                            put("name", "\$name")
                            put("classType", "\$lessons.classType")
                            put("teacher", "\$lessons.teacher")
                            put("position", "\$lessons.position")
                        })
                        .`as`("lessons"),

                // Data decoration
                project("lessons")
                        .and("_id").`as`("timePoint")
                        .andExclude("_id")

        )).mappedResults

    }
}
