package com.shinonometn.re.ssim.data.kingo.application.service

import com.mongodb.client.result.DeleteResult
import com.shinonometn.re.ssim.data.kingo.application.entity.CourseEntity
import com.shinonometn.re.ssim.data.kingo.application.repository.CourseRepository
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
open class CourseDataService(private val courseRepository: CourseRepository,
                             private val mongoTemplate: MongoTemplate) {

    fun query(aggregation: Aggregation): AggregationResults<Document> {
        return mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(CourseEntity::class.java), Document::class.java)
    }

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

}