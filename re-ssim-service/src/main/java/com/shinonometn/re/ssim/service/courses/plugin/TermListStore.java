package com.shinonometn.re.ssim.service.courses.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import com.shinonometn.re.ssim.service.courses.plugin.structure.TermMeta;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class TermListStore extends InMemoryStoreAdapter {

    private final String storeKey = cacheKey();
    private final HashOperations<String, String, TermMeta> store = redisTemplate.opsForHash();

    protected TermListStore(StringRedisTemplate redisTemplate) {
        super(redisTemplate, "term.info");
    }

    public Map<String, TermMeta> getAll() {
        if (!redisTemplate.hasKey(storeKey)) return null;

        return store.entries(storeKey);
    }

    public boolean isEmpty() {
        return redisTemplate.hasKey(storeKey);
    }

    public void update(String termName, TermMeta meta) {
        store.put(storeKey, termName, meta);
    }

    public void putAll(Map<String, TermMeta> termMetas) {
        store.putAll(storeKey, termMetas);
    }

    public TermMeta getTermMeta(String termName) {
        return Optional.of(store.get(storeKey,termName)).orElse(new TermMeta());
    }
}
