package com.shinonometn.re.ssim.service.courses;

import com.mongodb.client.result.DeleteResult;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.service.courses.entity.CourseEntity;
import com.shinonometn.re.ssim.service.courses.plugin.CourseTermListStore;
import com.shinonometn.re.ssim.service.courses.repository.CourseRepository;
import com.shinonometn.re.ssim.service.terms.SchoolTermInfoService;
import org.apache.commons.lang3.Range;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@SuppressWarnings("ALL")
@Service
public class CourseInfoService {

    private final MongoTemplate mongoTemplate;
    private final CourseRepository courseRepository;

    private final CourseTermListStore courseTermListStore;

    private final SchoolTermInfoService schoolTermInfoService;

    @Autowired
    public CourseInfoService(MongoTemplate mongoTemplate,
                             CourseRepository courseRepository,
                             SchoolTermInfoService schoolTermInfoService,
                             CourseTermListStore courseTermListStore) {

        this.mongoTemplate = mongoTemplate;
        this.courseRepository = courseRepository;
        this.schoolTermInfoService = schoolTermInfoService;
        this.courseTermListStore = courseTermListStore;
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
                .matching(Query.query(where("batchId").ne(currentVersion)))
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
                .matching(Query.query(where("batchId").is(version)))
                .all();
    }

    /**
     * Query teacher list by termName
     * <p>
     * Only get data of latest version
     *
     * @param termName termName
     * @return query result, list of teacher names
     */
    public Optional<List<String>> queryTermTeachers(String termName, String version) {

        Document queryResult = query(
                project("term", "batchId").and("lessons.teacher").as("teachers"),

                match(where("term").is(termName)
                        .and("batchId").is(version)),

                unwind("teachers"),

                group().addToSet("teachers").as("teachers"),

                project().andExclude("_id")
        ).getUniqueMappedResult();

        return queryResult == null ? Optional.empty() : Optional.of(queryResult.get("teachers", new ArrayList<>()));
    }

    /**
     * Query class list by termName
     * <p>
     * Only get data of latest version
     *
     * @param termName termName
     * @return query result, list of class names
     */
    public Optional<List<String>> queryTermClasses(String termName, String version) {
        Document queryResult = query(
                project("term", "batchId")
                        .and("lessons.classAttend").as("classAttend"),

                match(where("term").is(termName)
                        .and("batchId").is(version)),

                unwind("classAttend"),
                unwind("classAttend"),

                group().addToSet("classAttend").as("classes"),

                project().andExclude("_id")
        ).getUniqueMappedResult();

        return queryResult == null ? Optional.empty() : Optional.of(queryResult.get("classes", new ArrayList<>()));
    }

    /**
     * Query week range of a termName
     * <p>
     * Only get data of latest version
     *
     * @param termName termName name
     * @return query result
     */
    @SuppressWarnings("ConstantConditions")
    public Optional<Range<Integer>> queryTermWeekRange(String termName, String version) {
//        String version = schoolTermInfoService
//                .findByTermName(termName).orElse(new TermInfoEntity()).getDataVersion();

//        if(StringUtils.isEmpty(version)) version = null;

        Document document = query(
                project("term", "batchId").and("lessons.timePoint").as("timePoint"),

                match(where("term").is(termName).and("batchId").is(version)),

                unwind("timePoint"),
                unwind("timePoint"),

                group()
                        .max("timePoint.week").as("max")
                        .min("timePoint.week").as("min"),

                project().andExclude("_id")
        ).getUniqueMappedResult();

        return document == null ? Optional.empty() : Optional.of(Range.between(document.getInteger("min"), document.getInteger("max")));

    }

    /**
     * Query course list of a termName
     * <p>
     * Only get data of latest version
     *
     * @param termName termName
     * @return raw query result
     */
    public Optional<List<Document>> queryTermCourse(String termName, String version) {
        return Optional.ofNullable(executeAggregation(newAggregation(
                project("term", "code", "name", "unit", "lessons", "assessmentType", "batchId")
                        .and("lessons.classType").as("classType"),
                match(where("term").is(termName).and("batchId").is(version)),
                unwind("lessons"),
                unwind("classType"),
                group("code", "name", "unit", "classType", "assessmentType")
        )).getMappedResults());
    }

    /**
     * Query course type of a termName
     * <p>
     * Only get data of latest version
     *
     * @param termName termName
     * @return raw query result
     */
    public Optional<List<String>> queryTermCourseTypes(String termName, String version) {
//        String version = courseTermListStore.getTermMeta(termName).getDataVersion();

        Document queryResult = query(
                project("term", "batchId").and("lessons.classType").as("classType"),
                match(where("term").is(termName).and("batchId").is(version)),
                unwind("classType"),
                group().addToSet("classType").as("classTypes"),
                project("classTypes")
                        .andExclude("_id")
        ).getUniqueMappedResult();

        return queryResult == null ? Optional.empty() : Optional.of(queryResult.get("classTypes", new ArrayList<>()));
    }

    /**
     * Query classrooms that be used in a termName
     * <p>
     * Only get data of latest version
     *
     * @param termName termName
     * @return raw query result
     */
    public List<String> queryTermClassrooms(String termName, String version) {
        Document queryResult = query(
                project("term", "batchId").and("lessons.position").as("position"),
                match(where("term").is(termName).and("batchId").is(version)),
                unwind("position"),
                group().addToSet("position").as("position"),
                project("position")
                        .andExclude("_id")
        ).getUniqueMappedResult();

        return queryResult == null ? null : queryResult.get("position", new ArrayList<>());
    }

    /**
     * Query weeks that a class has lessons in a term
     *
     * @param termName term name
     * @param clazz    class name
     * @return list of week number
     */
    @NotNull
    public Optional<List<Integer>> queryWeeksOfClassByTerm(String termName, String clazz) {
        Document queryResult = query(
                project("term", "code", "name", "lessons"),

                unwind("lessons"),
                unwind("lessons.timePoint"),

                match(where("term").is(termName)
                        .and("lessons.classAttend").in(clazz)),

                group().addToSet("lessons.timePoint.week").as("weeks"),

                project("weeks").andExclude("_id")
        ).getUniqueMappedResult();

        return queryResult == null ? Optional.empty() : Optional.of(queryResult.get("weeks", new ArrayList<>()));
    }


    /**
     * Query weeks that a teacher has lessons in a term
     *
     * @param termName term name
     * @param teacher  teacher
     * @return list of week number
     */
    public Optional<List<Integer>> queryWeeksOfTeacherByTerm(String termName, String teacher) {
        Document queryResult = query(
                project("term", "code", "name", "lessons"),

                unwind("lessons"),
                unwind("lessons.timePoint"),

                match(where("term").is(termName)
                        .and("lessons.teacher").is(teacher)),

                group().addToSet("lessons.timePoint.week").as("weeks"),

                project("weeks").andExclude("_id")
        ).getUniqueMappedResult();

        return queryResult == null ? Optional.empty() : Optional.of(queryResult.get("weeks", new ArrayList<>()));
    }

    /**
     * Query database directly
     *
     * @param aggregationOperation aggregation operations
     * @return result
     */
    public AggregationResults<Document> query(AggregationOperation... aggregationOperation) {
        return mongoTemplate.aggregate(newAggregation(aggregationOperation), mongoTemplate.getCollectionName(CourseEntity.class), Document.class);
    }

    /*
     *
     * Private procedure
     *
     * */
}
