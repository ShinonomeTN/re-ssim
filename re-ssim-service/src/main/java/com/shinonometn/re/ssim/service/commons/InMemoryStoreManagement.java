package com.shinonometn.re.ssim.service.commons;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class InMemoryStoreManagement {

    @Value("${app.inMemoryDataKey:app\\:store}")
    private String keyPrefix = "app:store";

    private final String domain;

    protected final StringRedisTemplate redisTemplate;

    protected InMemoryStoreManagement(StringRedisTemplate redisTemplate, String domain) {
        this.redisTemplate = redisTemplate;
        this.domain = domain;
    }

    public final String domain() {
        return domain;
    }

    protected abstract void clear();

    protected String cacheKey() {
        return keyPrefix + ":" + domain();
    }
}
