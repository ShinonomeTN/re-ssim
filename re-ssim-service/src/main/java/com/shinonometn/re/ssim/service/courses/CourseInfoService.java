package com.shinonometn.re.ssim.service.courses;

import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.service.courses.entity.CourseEntity;
import com.shinonometn.re.ssim.service.courses.plugin.TermListStore;
import com.shinonometn.re.ssim.service.courses.plugin.TermMeta;
import com.shinonometn.re.ssim.service.courses.repository.CourseRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Service
public class CourseInfoService {

    private final MongoTemplate mongoTemplate;
    private final CourseRepository courseRepository;

    private final TermListStore termListStore;

    @Autowired
    public CourseInfoService(MongoTemplate mongoTemplate, CourseRepository courseRepository, TermListStore termListStore) {
        this.mongoTemplate = mongoTemplate;
        this.courseRepository = courseRepository;
        this.termListStore = termListStore;
    }

    /**
     * Is server has course data
     *
     * @return
     */
    @Cacheable(CacheKeys.SERVER_STATUS_COURSES_COUNT)
    public Boolean hasData() {
        return courseRepository.count() > 0;
    }

    public AggregationResults<Document> executeAggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(CourseEntity.class), Document.class);
    }

    public CourseEntity save(CourseEntity courseEntity) {
        return courseRepository.save(courseEntity);
    }

    public void delete(String id) {
        courseRepository.deleteById(id);
    }

    public Map<String,TermMeta> termList() {
        if(termListStore.isEmpty()){
            Map<String,TermMeta> queryResult = executeAggregation(newAggregation(
                    project("term"),
                    group("term").count().as("courseCount"),
                    project("courseCount")
                            .and("_id").as("name")
                    .andExclude("_id")
            )).getMappedResults().stream().collect(Collectors.toMap(
                    d -> d.getString("term"),
                    d -> new TermMeta(d.getInteger("courseCount"))
            ));

            termListStore.putAll(queryResult);
            return queryResult;
        } else {
            return termListStore.getAll();
        }
    }


}
