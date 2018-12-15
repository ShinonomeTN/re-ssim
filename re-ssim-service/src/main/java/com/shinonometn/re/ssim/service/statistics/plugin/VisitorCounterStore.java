package com.shinonometn.re.ssim.service.statistics.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class VisitorCounterStore extends InMemoryStoreAdapter<String> {

    private final ValueOperations<String, String> operations = redisTemplate.opsForValue();

    public VisitorCounterStore(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory, "counter.visitor");
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
