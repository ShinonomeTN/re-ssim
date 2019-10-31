package com.shinonometn.re.ssim.data.kingo.application.service

import org.apache.commons.lang3.Range
import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.stereotype.Service
import java.util.*
import java.util.Optional.ofNullable
import kotlin.collections.ArrayList

@Service
class TermDataService(private val courseDataService: CourseDataService) {

    /*
    *
    *
    *
    * */

    fun listAllCoursesOfTerm(termCode: String, version: String): Optional<List<Document>> = ofNullable(courseDataService.query(newAggregation(

            project("termCode", "code", "name", "unit", "lessons", "assessmentType", "batchId")
                    .and("lessons.classType").`as`("classType"),
            match(Criteria.where("termCode").`is`(termCode).and("batchId").`is`(version)),
            unwind("lessons"),
            unwind("classType"),
            group("code", "name", "unit", "classType", "assessmentType")

    )).mappedResults)

    fun listAllCourseTypesOfTerm(termCode: String, version: String): Optional<List<String>> = ofNullable(courseDataService.query(newAggregation(
            project("termCode", "batchId").and("lessons.classType").`as`("classType"),
            match(where("termCode").`is`(termCode).and("batchId").`is`(version)),
            unwind("classType"),
            group().addToSet("classType").`as`("classTypes"),
            project("classTypes")
                    .andExclude("_id")

    )).uniqueMappedResult).map { it.get("classTypes", ArrayList<String>()) }

    fun listClassesOfTerm(termCode: String, version: String): Optional<List<String>> = ofNullable(courseDataService.query(
            newAggregation(project("termCode", "batchId")
                    .and("lessons.classAttend").`as`("classAttend"),

                    match(where("termCode").`is`(termCode)
                            .and("batchId").`is`(version)),

                    unwind("classAttend"),
                    unwind("classAttend"),

                    group().addToSet("classAttend").`as`("classes"),

                    project().andExclude("_id"))
    ).uniqueMappedResult).map { it.get("classes", ArrayList<String>()) }

    fun listTeachersOfTerm(termCode: String, version: String): Optional<List<String>> = ofNullable(courseDataService.query(newAggregation(

            project("termCode", "batchId").and("lessons.teacher").`as`("teachers"),

            match(where("termCode").`is`(termCode)
                    .and("batchId").`is`(version)),

            unwind("teachers"),

            group().addToSet("teachers").`as`("teachers"),

            project().andExclude("_id"))

    ).uniqueMappedResult).map { it.get("teachers", ArrayList<String>()) }

    fun listClassroomsOfTerm(termCode: String, version: String): Optional<List<String>> = ofNullable(courseDataService.query(newAggregation(
            project("termCode", "batchId").and("lessons.position").`as`("position"),
            match(where("termCode").`is`(termCode).and("batchId").`is`(version)),
            unwind("position"),
            group().addToSet("position").`as`("position"),
            project("position")
                    .andExclude("_id")
    )).uniqueMappedResult).map { it.get("position", ArrayList<String>()) }

    fun getWeekRangeOfTerm(termCode: String, version: String): Optional<Range<Int>> = ofNullable(courseDataService.query(newAggregation(

            project("termCode", "batchId").and("lessons.timePoint").`as`("timePoint"),

            match(where("termCode").`is`(termCode).and("batchId").`is`(version)),

            unwind("timePoint"),
            unwind("timePoint"),

            group()
                    .max("timePoint.week").`as`("max")
                    .min("timePoint.week").`as`("min"),

            project().andExclude("_id"))

    ).uniqueMappedResult).map { Range.between<Int>(it.getInteger("min"), it.getInteger("max")) }

    fun countTermCourses(termCode: String, dataVersion: String): Optional<Int> = ofNullable(courseDataService.query(newAggregation(
            project("code", "termCode", "batchId"),
            match(where("termCode").`is`(termCode).and("batchId").`is`(dataVersion)),
            group("code"),
            count().`as`("count")
    )).uniqueMappedResult).map { it.getInteger("count") }

    fun listDepartmentsOfTerm(termCode: String, dataVersion: String): Optional<List<String>> = ofNullable(courseDataService.query(newAggregation(
            project("termCode", "unit", "batchId"),
            match(where("termCode").`is`(termCode).and("batchId").`is`(dataVersion)),
            group().addToSet("unit").`as`("departments"),
            project("departments").andExclude("_id")
    )).uniqueMappedResult).map { it.get("departments", ArrayList<String>()) }

}