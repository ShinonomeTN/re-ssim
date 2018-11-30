package com.shinonometn.re.ssim.service.statistics.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreManagement;
import org.jetbrains.annotations.NotNull;
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
        operations.setIfAbsent(cacheKey, "0");
        operations.increment(cacheKey, 1);
    }

    @NotNull
    public Long get() {
        operations.setIfAbsent(cacheKey, "0");
        return Long.valueOf(operations.get(cacheKey));
    }

}
