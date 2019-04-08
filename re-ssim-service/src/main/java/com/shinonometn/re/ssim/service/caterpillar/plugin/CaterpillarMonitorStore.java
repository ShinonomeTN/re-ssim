package com.shinonometn.re.ssim.service.caterpillar.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CaterpillarMonitorStore extends InMemoryStoreAdapter<Long> {

    private final HashOperations<String, String, Long> store = redisTemplate.opsForHash();

    private final static String KEY_CAPTURE_TASKS_COUNT = "captureTaskCount";
    private final static String KEY_IMPORT_TASK_COUNT = "importTaskCount";

    protected CaterpillarMonitorStore(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory, "monitor.caterpillar");
    }

    public void increaseCaptureTaskCount() {
        store.putIfAbsent(storeKey, KEY_CAPTURE_TASKS_COUNT, 0L);
        store.increment(storeKey, KEY_CAPTURE_TASKS_COUNT, 1);
    }

    public void decreaseCaptureTaskCount() {
        store.increment(storeKey, KEY_CAPTURE_TASKS_COUNT, -1);
    }

    public void increaseImportTaskCount() {
        store.putIfAbsent(storeKey, KEY_IMPORT_TASK_COUNT, 0L);
        store.increment(storeKey, KEY_IMPORT_TASK_COUNT, 1);
    }

    public void decreaseImportTaskCount() {
        store.increment(storeKey, KEY_CAPTURE_TASKS_COUNT, -1);
    }

    public Integer getImportTaskCount() {
        try {
            return Math.toIntExact(store.get(storeKey, KEY_IMPORT_TASK_COUNT));
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public Map<String, Long> getAll() {
        return store.entries(storeKey);
    }
}
