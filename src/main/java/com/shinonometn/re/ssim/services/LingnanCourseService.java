package com.shinonometn.re.ssim.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shinonometn.re.ssim.caterpillar.kingo.KingoUrls;
import com.shinonometn.re.ssim.caterpillar.kingo.capture.TermListPageProcessor;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.models.CaptureTask;
import com.shinonometn.re.ssim.repository.CaptureTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LingnanCourseService {

    private final Site site = Site.me()
            .setDomain("jwgl.lnc.edu.cn")
            .setTimeOut(5000)
            .setRetryTimes(3)
            .setSleepTime(500)
            .setCharset("GBK")
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8");

    private final CacheService cacheService;
    private final CaptureTaskRepository captureTaskRepository;

    @Autowired
    public LingnanCourseService(CacheService cacheService, CaptureTaskRepository captureTaskRepository) {

        this.cacheService = cacheService;
        this.captureTaskRepository = captureTaskRepository;
    }

    /**
     * Get all school terms
     * <p>
     * if cache not found, load from remote and cache it
     *
     * @return a map, term code as key, term name as value
     */
    public Map<String, String> getTermList() {

        Map<String, String> cachedResult = cacheService.get(CacheKeys.TERM_LIST, new TypeReference<Map<String, String>>() {
        });
        if (cachedResult != null) {
            return cachedResult;
        }

        final Map<String, String> capturedResult = new HashMap<>();

        Spider.create(new TermListPageProcessor(site))
                .addUrl(KingoUrls.classInfoQueryPage)
                .addPipeline((r, t) -> capturedResult.putAll(r.get(TermListPageProcessor.FIELD_TERMS)))
                .run();

        cacheService.put(CacheKeys.TERM_LIST, capturedResult);

        return capturedResult;
    }

    /**
     * Individual cache and re-capture list from remote
     *
     * @return see getTermList()
     */
    public Map<String, String> reloadAndGetTermList() {
        cacheService.expire(CacheKeys.TERM_LIST);
        return getTermList();
    }


    public List<CaptureTask> captureTasks() {

    }
}
