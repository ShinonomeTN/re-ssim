package com.shinonometn.re.ssim.service.courses.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import com.shinonometn.re.ssim.service.courses.plugin.structure.TermMeta;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
public class CourseTermListStore extends InMemoryStoreAdapter<TermMeta> {

    private final HashOperations<String, String, TermMeta> store = redisTemplate.opsForHash();

    protected CourseTermListStore(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory, "term.info");
    }

    public Collection<TermMeta> getAll() {
        if (!redisTemplate.hasKey(storeKey)) return null;

        return store.entries(storeKey).values();
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

    @SuppressWarnings("ConstantConditions")
    public TermMeta getTermMeta(String termName) {
        return Optional.ofNullable(store.get(storeKey, termName)).orElse(new TermMeta());
    }

    public boolean contains(String name) {
        return store.hasKey(storeKey,name);
    }
}
