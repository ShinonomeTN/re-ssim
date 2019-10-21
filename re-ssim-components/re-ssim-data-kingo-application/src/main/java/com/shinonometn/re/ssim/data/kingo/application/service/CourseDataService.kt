package com.shinonometn.re.ssim.data.kingo.application.service

import com.mongodb.client.result.DeleteResult
import com.shinonometn.re.ssim.data.kingo.application.entity.CourseEntity
import com.shinonometn.re.ssim.data.kingo.application.repository.CourseRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
open class CourseDataService(private val courseRepository: CourseRepository,
                        private val mongoTemplate: MongoTemplate) {

    open fun saveCourseInfo(courseEntity: CourseEntity) {
        courseRepository.save(courseEntity)
    }

    open fun deleteOtherVersions(currentVersion: Int): DeleteResult = mongoTemplate
            .remove(CourseEntity::class.java)
            .matching(Query.query(where("batchId").ne(currentVersion)))
            .all()

    open fun deleteVersion(version: Int): DeleteResult = mongoTemplate
            .remove(CourseEntity::class.java)
            .matching(Query.query(where("batchId").`is`(version)))
            .all()

}