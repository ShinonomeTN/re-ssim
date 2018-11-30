package com.shinonometn.re.ssim.service.caterpillar.commons;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreManagement;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CaterpillarMonitorPlugin extends InMemoryStoreManagement {

    private final HashOperations<String, String, String> store = redisTemplate.opsForHash();
    private final String cacheKey = cacheKey();

    private final static String KEY_CAPTURE_TASKS_COUNT = "captureTaskCount";
    private final static String KEY_IMPORT_TASK_COUNT = "importTaskCount";

    protected CaterpillarMonitorPlugin(StringRedisTemplate redisTemplate) {
        super(redisTemplate, "monitor-caterpillar");
    }

    public void increaseCaptureTaskCount() {
        store.putIfAbsent(cacheKey, KEY_CAPTURE_TASKS_COUNT, "0");
        store.increment(cacheKey, KEY_CAPTURE_TASKS_COUNT, 1);
    }

    public void decreaseCaptureTaskCount() {
        store.increment(cacheKey, KEY_CAPTURE_TASKS_COUNT, -1);
    }



    @Override
    protected void clear() {

    }

    public Integer getImportTaskCount() {
        return Integer.valueOf(store.get(cacheKey, KEY_CAPTURE_TASKS_COUNT));
    }

    public Map<String,String> getAll() {
        return store.entries(cacheKey);
    }
}
