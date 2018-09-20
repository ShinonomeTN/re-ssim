package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.services.CourseInfoService
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/term")
class CourseQueryController(@Autowired private val courseInfoService: CourseInfoService) {

    /**
     *
     * Query weeks of a class that has lessons
     *
     */
    @GetMapping("/{term}/class/{clazzName}/weeks")
    fun showClassTermWeeks(@PathVariable("term") term: String,
                           @PathVariable("clazzName") clazz: String): Any? =
            courseInfoService.executeAggregation(newAggregation(
                    project("term", "code", "name", "lessons"),

                    unwind("lessons"),
                    unwind("lessons.timePoint"),

                    match(where("term").`is`(term)
                            .and("lessons.classAttend").`in`(clazz)),

                    group().addToSet("lessons.timePoint.week").`as`("weeks"),

                    project("weeks").andExclude("_id")
            )).uniqueMappedResult

    /**
     *
     * Query class lessons by predicates
     *
     */
    @GetMapping("/{term}/class/{class}/course")
    @ResponseBody
    fun queryClassWeekCourses(@PathVariable("term") term: String,
                              @PathVariable("class") clazz: String,
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

                    // Find right lessons that matched given class
                    unwind("lessons.classAttend"),
                    match(where("lessons.classAttend").`is`(clazz)),

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





    /**
     *
     * Query weeks of a class that has lessons
     *
     */
    @GetMapping("/{term}/teacher/{teacher}/weeks")
    fun showTeacherTermWeeks(@PathVariable("term") term: String,
                           @PathVariable("teacher") teacher: String): Any? =
            courseInfoService.executeAggregation(newAggregation(
                    project("term", "code", "name", "lessons"),

                    unwind("lessons"),
                    unwind("lessons.timePoint"),

                    match(where("term").`is`(term)
                            .and("lessons.teacher").`is`(teacher)),

                    group().addToSet("lessons.timePoint.week").`as`("weeks"),

                    project("weeks").andExclude("_id")
            )).uniqueMappedResult

    /**
     *
     * Query teacher lessons by predicates
     *
     */
    @GetMapping("/{term}/teacher/{teacher}/course")
    fun queryTeacherWeekCourses(@PathVariable("term") term: String,
                                @PathVariable("teacher")teacher: String,
                                @RequestParam("week") week: Int): Any =
            courseInfoService.executeAggregation(newAggregation(

                    // Project necessary fields
                    project("term", "code", "name", "lessons"),

                    // Find matched lesson
                    match(where("term").`is`(term)
                            .and("lessons.teacher").`in`(teacher)),

                    // Explain records
                    unwind("lessons"),
                    unwind("lessons.timePoint"),

                    match(where("lessons.timePoint.week").`is`(week)),

                    group("lessons.timePoint")
                            .addToSet(Document().apply {
                                put("code", "\$code")
                                put("name", "\$name")
                                put("classType", "\$lessons.classType")
                                put("position", "\$lessons.position")
                            }).`as`("lessons"),

                    project("lessons")
                            .and("_id").`as`("timePoint")
                            .andExclude("_id")
            )).mappedResults
}
