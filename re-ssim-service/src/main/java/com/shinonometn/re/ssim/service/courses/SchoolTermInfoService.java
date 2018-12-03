package com.shinonometn.re.ssim.service.courses;

import com.shinonometn.re.ssim.service.caterpillar.common.SchoolCalendar;
import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting;
import com.shinonometn.re.ssim.service.caterpillar.kingo.KingoSchoolCalendar;
import com.shinonometn.re.ssim.service.caterpillar.kingo.KingoUrls;
import com.shinonometn.re.ssim.service.caterpillar.kingo.capture.CalendarListPageProcessor;
import com.shinonometn.re.ssim.service.caterpillar.kingo.capture.TermListPageProcessor;
import com.shinonometn.re.ssim.service.courses.entity.CourseEntity;
import com.shinonometn.re.ssim.service.courses.entity.SchoolCalendarEntity;
import com.shinonometn.re.ssim.service.courses.entity.TermInfoEntity;
import com.shinonometn.re.ssim.service.courses.plugin.structure.TermMeta;
import com.shinonometn.re.ssim.service.courses.repository.TermInfoRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.*;
import java.util.stream.Collectors;

import static com.shinonometn.re.ssim.service.caterpillar.kingo.KingoUrls.calendarPage;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class SchoolTermInfoService {
    private final TermInfoRepository termInfoRepository;
    private final MongoTemplate mongoTemplate;

    public SchoolTermInfoService(TermInfoRepository termInfoRepository, MongoTemplate mongoTemplate) {
        this.termInfoRepository = termInfoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public TermInfoEntity get(String id) {
        return termInfoRepository.findById(id).orElse(null);
    }

    public Page<TermInfoEntity> list(Pageable pageable) {
        return termInfoRepository.findAll(pageable);
    }

    public TermInfoEntity save(TermInfoEntity entry) {
        entry.setUpdateDate(new Date());
        return termInfoRepository.save(entry);
    }

    /**
     * Save all terms, set updateDate before saving
     *
     * @param entities entries
     * @return saved entries
     */
    public List<TermInfoEntity> saveAll(Collection<TermInfoEntity> entities) {
        entities.forEach(e -> e.setUpdateDate(new Date()));
        return termInfoRepository.saveAll(entities);
    }

    /**
     * Pull termName list from remote school calendar
     *
     * @return key: termName code, value: termName name
     */
    public Map<String, String> pull() {
        Map<String, String> resultSet = new HashMap<>();

        Spider.create(new CalendarListPageProcessor(site))
                .addUrl(calendarPage)
                .addPipeline((resultItems, task) -> resultSet.putAll(CalendarListPageProcessor.getCalendarList(resultItems)))
                .run();

        // Update database
        saveAll(resultSet.entrySet()
                .stream()
                .map(e -> {
                    TermInfoEntity entry = termInfoRepository.findByName(e.getKey()).orElse(new TermInfoEntity());
                    entry.setCode(e.getKey());
                    entry.setName(e.getValue());
                    return entry;
                })
                .collect(Collectors.toList()));

        return resultSet;
    }

    /**
     * Update school terms that has courses
     */
    public void pullCoursesListTerm() {

        Spider.create(new TermListPageProcessor(CaterpillarSetting.Companion.createDefaultSite()))
                .addUrl(KingoUrls.classInfoQueryPage)
                .addPipeline((r, t) -> saveAll(TermListPageProcessor
                        .getTerms(r)
                        .entrySet()
                        .stream()
                        .map(e -> termInfoRepository
                                .findByCode(e.getKey())
                                .orElse(termInfoRepository
                                        .findByName(e.getValue())
                                        .orElseGet(() -> {
                                            TermInfoEntity newEntity = new TermInfoEntity();
                                            newEntity.setCode(e.getKey());
                                            newEntity.setName(e.getValue());
                                            return newEntity;
                                        }))).collect(Collectors.toList())))
                .run();
    }

    /**
     * Query a list of term name that has local course data
     *
     * @return raw query result list
     * <p>
     * keys:
     * "term" contains a String,
     * "courseCount" contains a Integer
     */
    public List<Document> queryTermsHasCourses() {
        return courseQuery(
                project("term"),
                group("term").count().as("courseCount"),
                project("courseCount")
                        .and("_id").as("name")
                        .andExclude("_id"))
                .getMappedResults();
    }

    /*
     *
     * Private procedure
     *
     * */

    private AggregationResults<Document> courseQuery(AggregationOperation... aggregationOperation) {
        return mongoTemplate.aggregate(newAggregation(aggregationOperation), mongoTemplate.getCollectionName(CourseEntity.class), Document.class);
    }

    private Site site = Site.me()
            .setDomain("jwgl.lnc.edu.cn")
            .setTimeOut(10000)
            .setRetryTimes(81)
            .setSleepTime(500)
            .setCharset("GBK")
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
            .addHeader("Referer", calendarPage);
}
