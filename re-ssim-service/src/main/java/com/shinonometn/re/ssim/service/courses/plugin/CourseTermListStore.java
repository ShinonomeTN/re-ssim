package com.shinonometn.re.ssim.service.courses.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import com.shinonometn.re.ssim.service.courses.plugin.structure.TermMeta;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
public class CourseTermListStore extends InMemoryStoreAdapter<TermMeta> {

    private final BoundHashOperations<String,String,TermMeta> store;

    protected CourseTermListStore(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory, "term.info");
        store = redisTemplate.boundHashOps(storeKey);
    }

    public Collection<TermMeta> getAll() {
        if (!redisTemplate.hasKey(storeKey)) return null;

        return store.values();
    }

    public boolean isEmpty() {
        return !redisTemplate.hasKey(storeKey);
    }

    public void update(String termName, TermMeta meta) {
        store.put(termName, meta);
    }

    public void putAll(Map<String, TermMeta> termMetas) {
        store.putAll(termMetas);
    }

    @SuppressWarnings("ConstantConditions")
    public TermMeta getTermMeta(String termName) {
        return Optional.ofNullable(store.get(termName)).orElse(new TermMeta());
    }

    public boolean contains(String name) {
        return store.hasKey(name);
    }
}
