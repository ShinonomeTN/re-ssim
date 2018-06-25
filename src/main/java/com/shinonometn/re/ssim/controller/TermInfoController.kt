package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.commons.CacheKeys
import com.shinonometn.re.ssim.services.CacheService
import com.shinonometn.re.ssim.services.CourseInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/api/term")
open class TermInfoController @Autowired
constructor(private val courseInfoService: CourseInfoService,
            private val cacheService: CacheService) {

    /**
     *
     * List all terms
     *
     */
    @GetMapping
    @ResponseBody
    @Cacheable(CacheKeys.TERM_LIST)
    open fun list(): Any = courseInfoService.executeAggregation(newAggregation(
            project("term"),
            group("term").count().`as`("courseCount")
    )).mappedResults

    /**
     *
     * List term courses
     *
     */
    @GetMapping("/{name}", params = ["course"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_COURSE_LIST)
    open fun listTermCourse(@PathVariable("name") termName: String): Any =
            courseInfoService.executeAggregation(newAggregation(
                    match(Criteria.where("term").`is`(termName)),
                    project("code", "name", "unit", "lessons"),
                    unwind("lessons"),
                    group("code", "name", "unit").count().`as`("courseCount")
            )).mappedResults

    /**
     *
     * List all teacher presented in term
     *
     */
    @GetMapping("/{name}", params = ["teacher"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_TEACHER_LIST)
    open fun listTermTeachers(@PathVariable("name") termName: String): Any =
            courseInfoService.executeAggregation(newAggregation(
                    match(Criteria.where("term").`is`(termName)),
                    unwind("lessons"),
                    group("lessons.teacher"),
                    project()
                            .and("_id").`as`("name")
                            .andExclude("_id")
            )).mappedResults

    /**
     *
     * List all term class
     *
     */
    @GetMapping("/{name}", params = ["class"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_CLASS_LIST)
    open fun listTermClasses(@PathVariable("name") termName: String): Any =
            courseInfoService.executeAggregation(newAggregation(
                    match(Criteria.where("term").`is`(termName)),
                    group("lessons.classAttend"),
                    unwind("_id"),
                    unwind("_id"),
                    group("_id"),
                    project()
                            .and("_id").`as`("name")
                            .andExclude("_id")
            )).mappedResults

    /**
     *
     * Show term week range
     *
     */
    @GetMapping("/{name}", params = ["weekRange"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_WEEK_RANGE)
    open fun showTermWeekRange(@PathVariable("name") termName: String): Any =
            courseInfoService.executeAggregation(newAggregation(
                    match(Criteria.where("term").`is`(termName)),
                    unwind("lessons"),
                    group("lessons.timePoint"),
                    unwind("_id"),
                    group()
                            .max("_id.week").`as`("max")
                            .min("_id.week").`as`("min"),
                    project("max", "min")
                            .andExclude("_id")
            )).mappedResults
}
