package com.shinonometn.re.ssim.controller

import com.shinonometn.re.ssim.commons.CacheKeys
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
constructor(private val courseInfoService: CourseInfoService) {

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
            group("term").count().`as`("courseCount"),
            project("courseCount")
                    .and("_id").`as`("name")
                    .andExclude("_id")
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
                    project("term", "code", "name", "unit", "lessons", "assessmentType")
                            .and("lessons.classType").`as`("classType"),
                    match(Criteria.where("term").`is`(termName)),
                    unwind("lessons"),
                    group("code", "name", "unit", "classType")
                            .count().`as`("courseCount"),
                    unwind("classType"),
                    group("code", "name", "unit", "classType", "courseCount", "assessmentType")
            )).mappedResults

    /**
     *
     * List all teacher presented in term
     *
     */
    @GetMapping("/{name}", params = ["teacher"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_TEACHER_LIST)
    open fun listTermTeachers(@PathVariable("name") termName: String): Any? =
            courseInfoService.executeAggregation(newAggregation(
                    project("term","lessons.teacher"),
                    match(Criteria.where("term").`is`(termName)),
                    unwind("lessons"),
                    group().addToSet("lessons.teacher").`as`("teachers"),
                    project("teachers")
                            .andExclude("_id")
            )).uniqueMappedResult

    /**
     *
     * List all term class
     *
     */
    @GetMapping("/{name}", params = ["class"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_CLASS_LIST)
    open fun listTermClasses(@PathVariable("name") termName: String): Any? =
            courseInfoService.executeAggregation(newAggregation(
                    match(Criteria.where("term").`is`(termName)),
                    unwind("lessons"),
                    group("lessons.classAttend"),
                    unwind("_id"),
                    group().addToSet("_id").`as`("classes"),
                    project("classes")
                            .andExclude("_id")
            )).uniqueMappedResult

    /**
     *
     * Show term week range
     *
     */
    @GetMapping("/{name}", params = ["weekRange"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_WEEK_RANGE)
    open fun showTermWeekRange(@PathVariable("name") termName: String): Any? =
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
            )).uniqueMappedResult

    /**
     *
     * Get all class types of term
     *
     */
    @GetMapping("/{name}", params = ["classType"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_CLASS_TYPE)
    open fun listTermClassTypes(@PathVariable("name") termName: String): Any? =
            courseInfoService.executeAggregation(newAggregation(
                    match(Criteria.where("term").`is`(termName)),
                    unwind("lessons"),
                    group().addToSet("lessons.classType").`as`("classTypes"),
                    project("classTypes")
                            .andExclude("_id")
            )).uniqueMappedResult

    /**
     *
     * List all classrooms that used in term
     *
     */
    @GetMapping("/{name}", params = ["classroom"])
    @ResponseBody
    @Cacheable(CacheKeys.TERM_CLASSROOM)
    open fun listTermClassrooms(@PathVariable("name") termName: String): Any? =
            courseInfoService.executeAggregation(newAggregation(
                    match(Criteria.where("term").`is`(termName)),
                    unwind("lessons"),
                    group().addToSet("lessons.position").`as`("position"),
                    project("position")
                            .andExclude("_id")
            )).uniqueMappedResult
}
