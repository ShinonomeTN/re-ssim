package com.shinonometn.re.ssim.services;

import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.models.CourseEntity;
import com.shinonometn.re.ssim.repository.CourseRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

@Service
public class CourseInfoService {

    private final MongoTemplate mongoTemplate;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseInfoService(MongoTemplate mongoTemplate, CourseRepository courseRepository) {
        this.mongoTemplate = mongoTemplate;
        this.courseRepository = courseRepository;
    }

    @Cacheable(CacheKeys.SERVER_STATUS_COURSES_COUNT)
    public Boolean hasData(){
        return courseRepository.count() > 0;
    }

    public AggregationResults<Document> executeAggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(CourseEntity.class), Document.class);
    }
}
