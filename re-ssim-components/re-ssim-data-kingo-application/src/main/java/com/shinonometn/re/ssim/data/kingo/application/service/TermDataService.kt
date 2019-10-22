package com.shinonometn.re.ssim.data.kingo.application.service

import org.apache.commons.lang3.Range
import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.stereotype.Service
import java.util.*
import java.util.Optional.ofNullable

@Service
class TermDataService(private val courseDataService: CourseDataService) {

    fun listAllCoursesOfTerm(termName: String, version: String): Optional<List<Document>> = ofNullable(courseDataService.query(newAggregation(

            project("term", "code", "name", "unit", "lessons", "assessmentType", "batchId")
                    .and("lessons.classType").`as`("classType"),
            match(Criteria.where("term").`is`(termName).and("batchId").`is`(version)),
            unwind("lessons"),
            unwind("classType"),
            group("code", "name", "unit", "classType", "assessmentType")

    )).mappedResults)

    fun listAllCourseTypesOfTerm(termName: String, version: String): Optional<List<String>> = ofNullable(courseDataService.query(newAggregation(
            project("term", "batchId").and("lessons.classType").`as`("classType"),
            match(where("term").`is`(termName).and("batchId").`is`(version)),
            unwind("classType"),
            group().addToSet("classType").`as`("classTypes"),
            project("classTypes")
                    .andExclude("_id")
    )).uniqueMappedResult).map { it.get("classTypes", ArrayList<String>()) }

    fun listClassesOfTerm(termName: String, version: String): Optional<List<String>> = ofNullable(courseDataService.query(
            newAggregation(project("term", "batchId")
                    .and("lessons.classAttend").`as`("classAttend"),

                    match(where("term").`is`(termName)
                            .and("batchId").`is`(version)),

                    unwind("classAttend"),
                    unwind("classAttend"),

                    group().addToSet("classAttend").`as`("classes"),

                    project().andExclude("_id"))
    ).uniqueMappedResult).map { it.get("classes", ArrayList<String>()) }

    fun listTeachersOfTerm(termName: String, version: String): Optional<List<String>> = ofNullable(courseDataService.query(newAggregation(

            project("term", "batchId").and("lessons.teacher").`as`("teachers"),

            match(where("term").`is`(termName)
                    .and("batchId").`is`(version)),

            unwind("teachers"),

            group().addToSet("teachers").`as`("teachers"),

            project().andExclude("_id"))

    ).uniqueMappedResult).map { it.get("teachers", ArrayList<String>()) }

    fun listClassroomsOfTerm(termName: String, version: String): Optional<List<String>> = ofNullable(courseDataService.query(newAggregation(
            project("term", "batchId").and("lessons.position").`as`("position"),
            match(where("term").`is`(termName).and("batchId").`is`(version)),
            unwind("position"),
            group().addToSet("position").`as`("position"),
            project("position")
                    .andExclude("_id")
    )).uniqueMappedResult).map { it.get("position", ArrayList<String>()) }

    fun getWeekRangeOfTerm(termName: String, version: String): Optional<Range<Int>> = ofNullable(courseDataService.query(newAggregation(

            project("term", "batchId").and("lessons.timePoint").`as`("timePoint"),

            match(where("term").`is`(termName).and("batchId").`is`(version)),

            unwind("timePoint"),
            unwind("timePoint"),

            group()
                    .max("timePoint.week").`as`("max")
                    .min("timePoint.week").`as`("min"),

            project().andExclude("_id"))

    ).uniqueMappedResult).map { Range.between<Int>(it.getInteger("min"), it.getInteger("max")) }

}