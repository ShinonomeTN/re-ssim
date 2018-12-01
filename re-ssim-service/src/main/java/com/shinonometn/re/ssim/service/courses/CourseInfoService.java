package com.shinonometn.re.ssim.service.courses;

import com.mongodb.client.result.DeleteResult;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.service.caterpillar.ImportTaskService;
import com.shinonometn.re.ssim.service.courses.entity.CourseEntity;
import com.shinonometn.re.ssim.service.courses.plugin.TermListStore;
import com.shinonometn.re.ssim.service.courses.plugin.structure.TermMeta;
import com.shinonometn.re.ssim.service.courses.repository.CourseRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class CourseInfoService {

    private final MongoTemplate mongoTemplate;
    private final CourseRepository courseRepository;
    private final ImportTaskService importTaskService;

    private final TermListStore termListStore;

    @Autowired
    public CourseInfoService(MongoTemplate mongoTemplate,
                             CourseRepository courseRepository,
                             ImportTaskService importTaskService, TermListStore termListStore) {

        this.mongoTemplate = mongoTemplate;
        this.courseRepository = courseRepository;
        this.importTaskService = importTaskService;
        this.termListStore = termListStore;
    }

    /**
     * Is server has course data
     *
     * @return boolean
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

    /**
     * Delete course info excepted if match version
     *
     * @param currentVersion version to exclude
     * @return delete result
     */
    public DeleteResult deleteOtherVersions(String currentVersion) {
        return mongoTemplate
                .remove(CourseEntity.class)
                .matching(Query.query(Criteria.where("batchId").ne(currentVersion)))
                .all();
    }

    /**
     * Delete course info by version
     *
     * @param version version , batchID
     * @return delete result
     */
    public DeleteResult deleteVersion(String version) {
        return mongoTemplate
                .remove(CourseEntity.class)
                .matching(Query.query(Criteria.where("batchId").is(version)))
                .all();
    }

    /**
     * Get term list
     *
     * @return term list with meta
     */

    public Map<String, TermMeta> termList() {
        if (termListStore.isEmpty()) {
            Map<String, TermMeta> queryResult = queryTermInfoList()
                    .stream()
                    .collect(Collectors.toMap(d -> d.getString("name"), this::termMetaFromDocument));

            termListStore.putAll(queryResult);
            return queryResult;
        } else {
            return termListStore.getAll();
        }
    }

    private TermMeta termMetaFromDocument(Document document) {
        TermMeta termMeta = new TermMeta();
        termMeta.setCourseCount(document.getInteger("courseCount"));
        termMeta.setVersion(importTaskService.latestVersionOf(document.getString("name")));
        return termMeta;
    }

    /**
     * Query term info from database
     *
     * @return raw query result
     */
    public List<Document> queryTermInfoList() {
        return executeAggregation(newAggregation(
                project("term"),
                group("term").count().as("courseCount"),
                project("courseCount")
                        .and("_id").as("name")
                        .andExclude("_id")
        )).getMappedResults();
    }

    /**
     * Query teacher list by term
     * <p>
     * Only get data of latest version
     *
     * @param termName term
     * @return raw query result
     */
    public Document queryTermTeachers(String termName) {
        String version = termListStore.getTermMeta(termName).getVersion();

        return executeAggregation(newAggregation(
                project("term", "batchId").and("lessons.teacher").as("teachers"),
                match(Criteria.where("term").is(termName).and("batchId").is(version)),
                unwind("teachers"),
                group().addToSet("teachers").as("teachers"),
                project().andExclude("_id")
        )).getUniqueMappedResult();
    }

    /**
     * Query class list by term
     * <p>
     * Only get data of latest version
     *
     * @param termName term
     * @return raw query result
     */
    public Document queryTermClasses(String termName) {
        String version = termListStore.getTermMeta(termName).getVersion();

        return executeAggregation(newAggregation(
                project("term", "batchId").and("lessons.classAttend").as("classAttend"),
                match(Criteria.where("term").is(termName).and("batchId").is(version)),
                unwind("classAttend"),
                unwind("classAttend"),
                group().addToSet("classAttend").as("classes"),
                project().andExclude("_id")
        )).getUniqueMappedResult();
    }

    /**
     * Query week range of a term
     * <p>
     * Only get data of latest version
     *
     * @param termName term name
     * @return raw query result
     */
    public Document queryTermWeekRange(String termName) {
        String version = termListStore.getTermMeta(termName).getVersion();

        return executeAggregation(newAggregation(
                project("term", "batchId").and("lessons.timePoint").as("timePoint"),
                match(Criteria.where("term").is(termName).and("batchId").is(version)),
                unwind("timePoint"),
                unwind("timePoint"),
                group()
                        .max("timePoint.week").as("max")
                        .min("timePoint.week").as("min"),
                project().andExclude("_id")
        )).getUniqueMappedResult();
    }

    /**
     * Query course list of a term
     * <p>
     * Only get data of latest version
     *
     * @param termName term
     * @return raw query result
     */
    public List<Document> queryTermCourse(String termName) {
        String version = termListStore.getTermMeta(termName).getVersion();

        return executeAggregation(newAggregation(
                project("term", "code", "name", "unit", "lessons", "assessmentType", "batchId")
                        .and("lessons.classType").as("classType"),
                match(Criteria.where("term").is(termName).and("batchId").is(version)),
                unwind("lessons"),
                unwind("classType"),
                group("code", "name", "unit", "classType", "assessmentType")
        )).getMappedResults();
    }

    /**
     * Query course type of a term
     * <p>
     * Only get data of latest version
     *
     * @param termName term
     * @return raw query result
     */
    public Document queryTermCourseTypes(String termName) {
        String version = termListStore.getTermMeta(termName).getVersion();

        return executeAggregation(newAggregation(
                project("term", "batchId").and("lessons.classType").as("classType"),
                match(Criteria.where("term").is(termName).and("batchId").is(version)),
                unwind("classType"),
                group().addToSet("classType").as("classTypes"),
                project("classTypes")
                        .andExclude("_id")
        )).getUniqueMappedResult();
    }

    /**
     * Query classrooms that be used in a term
     * <p>
     * Only get data of latest version
     *
     * @param termName term
     * @return raw query result
     */
    public Document queryTermClassrooms(String termName) {
        String version = termListStore.getTermMeta(termName).getVersion();

        return executeAggregation(newAggregation(
                project("term", "batchId").and("lessons.position").as("position"),
                match(Criteria.where("term").is(termName).and("batchId").is(version)),
                unwind("position"),
                group().addToSet("position").as("position"),
                project("position")
                        .andExclude("_id")
        )).getUniqueMappedResult();
    }
}
