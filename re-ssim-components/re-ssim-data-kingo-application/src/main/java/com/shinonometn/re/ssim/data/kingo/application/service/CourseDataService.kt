package com.shinonometn.re.ssim.data.kingo.application.service

import com.mongodb.client.result.DeleteResult
import com.shinonometn.re.ssim.data.kingo.application.entity.CourseEntity
import com.shinonometn.re.ssim.data.kingo.application.repository.CourseRepository
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.util.*
import java.util.Optional.ofNullable

@Service
open class CourseDataService(private val courseRepository: CourseRepository,
                             private val mongoTemplate: MongoTemplate) {

    open fun saveCourseInfo(courseEntity: CourseEntity) {
        courseRepository.save(courseEntity)
    }

    open fun deleteOtherVersions(currentVersion: String): DeleteResult = mongoTemplate
            .remove(CourseEntity::class.java)
            .matching(Query.query(where("batchId").ne(currentVersion)))
            .all()

    open fun deleteVersion(version: String): DeleteResult = mongoTemplate
            .remove(CourseEntity::class.java)
            .matching(Query.query(where("batchId").`is`(version)))
            .all()

    /*
    *
    * Querying
    *
    * */

    fun query(aggregation: Aggregation): AggregationResults<Document> {
        return mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(CourseEntity::class.java), Document::class.java)
    }

    fun queryWeeksOfClassByTerm(term: String, clazz: String): Optional<ArrayList<String>> = ofNullable(query(newAggregation(

            project("term", "code", "name", "lessons"),

            unwind("lessons"),
            unwind("lessons.timePoint"),

            match(where("term").`is`(term)
                    .and("lessons.classAttend").`in`(clazz)),

            group().addToSet("lessons.timePoint.week").`as`("weeks"),

            project("weeks").andExclude("_id")

    )).uniqueMappedResult).map { it.get("weeks", ArrayList<String>()) }

    fun queryWeeksOfTeacherByTerm(termName: String, teacher: String): Optional<List<Int>> = ofNullable(query(newAggregation(

            project("term", "code", "name", "lessons"),

            unwind("lessons"),
            unwind("lessons.timePoint"),

            match(where("term").`is`(termName)
                    .and("lessons.teacher").`is`(teacher)),

            group().addToSet("lessons.timePoint.week").`as`("weeks"),

            project("weeks").andExclude("_id")

    )).uniqueMappedResult).map { it.get("weeks", ArrayList<Int>()) }


}