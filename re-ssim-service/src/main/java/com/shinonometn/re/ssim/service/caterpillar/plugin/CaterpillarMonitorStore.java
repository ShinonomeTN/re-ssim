package com.shinonometn.re.ssim.service.caterpillar.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CaterpillarMonitorStore extends InMemoryStoreAdapter {

    private final HashOperations<String, String, String> store = redisTemplate.opsForHash();
    private final String cacheKey = cacheKey();

    private final static String KEY_CAPTURE_TASKS_COUNT = "captureTaskCount";
    private final static String KEY_IMPORT_TASK_COUNT = "importTaskCount";

    protected CaterpillarMonitorStore(StringRedisTemplate redisTemplate) {
        super(redisTemplate, "monitor.caterpillar");
    }

    public void increaseCaptureTaskCount() {
        store.putIfAbsent(cacheKey, KEY_CAPTURE_TASKS_COUNT, "0");
        store.increment(cacheKey, KEY_CAPTURE_TASKS_COUNT, 1);
    }

    public void decreaseCaptureTaskCount() {
        store.increment(cacheKey, KEY_CAPTURE_TASKS_COUNT, -1);
    }

    public Integer getImportTaskCount() {
        return Integer.valueOf(store.get(cacheKey, KEY_CAPTURE_TASKS_COUNT));
    }

    public Map<String,String> getAll() {
        return store.entries(cacheKey);
    }
}
