package com.shinonometn.re.ssim.service.statistics.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class VisitorCounterStore extends InMemoryStoreAdapter {

    private final ValueOperations<String, String> operations;

    private final String storeKey = cacheKey();

    public VisitorCounterStore(StringRedisTemplate template) {
        super(template, "counter.visitor");
        operations = template.opsForValue();
    }

    @Override
    public void clear() {
        operations.set(domain(), "0");
    }

    public void increase() {
        operations.setIfAbsent(storeKey, "0");
        operations.increment(storeKey, 1);
    }

    @NotNull
    public Long get() {
        operations.setIfAbsent(storeKey, "0");
        return Long.valueOf(operations.get(storeKey));
    }

}
