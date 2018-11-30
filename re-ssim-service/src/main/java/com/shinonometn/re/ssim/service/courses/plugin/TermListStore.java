package com.shinonometn.re.ssim.service.courses.plugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shinonometn.re.ssim.commons.JSON;
import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TermListStore extends InMemoryStoreAdapter {

    private final String storeKey = cacheKey();
    private final HashOperations<String, String, String> store = redisTemplate.opsForHash();

    protected TermListStore(StringRedisTemplate redisTemplate) {
        super(redisTemplate, "term.info");
    }

    public Map<String, TermMeta> getAll() {
        final TypeReference<TermMeta> typeReference = new TypeReference<TermMeta>() {
        };

        if (!redisTemplate.hasKey(storeKey)) return null;

        return store.entries(storeKey)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            try {
                                return JSON.read(e.getValue(), typeReference);
                            } catch (IOException e1) {
                                return null;
                            }
                        }
                ));
    }

    public boolean isEmpty() {
        return redisTemplate.hasKey(storeKey);
    }

    public void update(String courseName, TermMeta meta) {
        String json = JSON.parse(meta);
        store.put(storeKey, courseName, json);
    }

    public void putAll(Map<String, TermMeta> queryResult) {
        store.putAll(storeKey, queryResult.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, JSON::parse)));
    }
}
