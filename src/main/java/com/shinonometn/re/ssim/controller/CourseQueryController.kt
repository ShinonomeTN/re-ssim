package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.services.CourseInfoService
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/term")
class CourseQueryController(@Autowired private val courseInfoService: CourseInfoService) {

    @GetMapping("/{term}/course", params = ["class", "week"])
    @ResponseBody
    fun queryClassWeekCourses(@PathVariable("term") term: String,
                              @RequestParam("class", required = true) clazz: String,
                              @RequestParam("week", required = true) week: Int,
                              @RequestParam("excludedType", required = false) excludedType: List<String>?): Any =
            courseInfoService.executeAggregation(newAggregation(

                    // Project necessary fields
                    project("term", "code", "name", "lessons"),

                    // Find matched lesson
                    match(where("term").`is`(term)
                            .and("lessons.classAttend").`in`(clazz).also {

                                // If exclude list specified
                                if (excludedType != null)
                                    it.and("lessons.classType").nin(excludedType)
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
