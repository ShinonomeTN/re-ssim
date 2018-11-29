package com.shinonometn.re.ssim.service.statistics.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreManagement;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class VisitorCounterPlugin extends InMemoryStoreManagement {

    private final ValueOperations<String, String> operations;

    private final String cacheKey = cacheKey();

    public VisitorCounterPlugin(StringRedisTemplate template) {
        super(template, "counter-visitor");
        operations = template.opsForValue();
    }

    @Override
    protected void clear() {
        operations.set(domain(), "0");
    }

    public void increase() {
        final String key = cacheKey;

        operations.setIfAbsent(key, "0");
        operations.increment(key, 1);
    }

    public Long get() {
        return Long.valueOf(operations.get(cacheKey));
    }

}
