package com.shinonometn.re.ssim.service.commons;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class InMemoryStoreAdapter implements InMemoryStore {

    @Value("${app.inMemoryDataKey:app\\:store}")
    private String keyPrefix = "app:store";

    private final String domain;

    protected final StringRedisTemplate redisTemplate;

    protected InMemoryStoreAdapter(StringRedisTemplate redisTemplate, String domain) {
        this.redisTemplate = redisTemplate;
        this.domain = domain;
    }

    @Override
    public final String domain() {
        return domain;
    }

    @Override
    public void clear() {
        redisTemplate.delete(cacheKey());
    }

    protected String cacheKey() {
        return keyPrefix + ":" + domain();
    }
}
