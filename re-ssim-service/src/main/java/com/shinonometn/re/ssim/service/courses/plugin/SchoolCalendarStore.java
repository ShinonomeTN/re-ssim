package com.shinonometn.re.ssim.service.courses.plugin;

import com.shinonometn.re.ssim.service.caterpillar.common.SchoolDate;
import com.shinonometn.re.ssim.service.commons.InMemoryStoreAdapter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class SchoolCalendarStore extends InMemoryStoreAdapter<SchoolDate> {

    private final ValueOperations<String, SchoolDate> store;

    private Supplier<SchoolDate> schoolDateSupplier = null;

    protected SchoolCalendarStore(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory, "schoolDate");
        store = redisTemplate.opsForValue();
    }

    public void setSchoolDateSupplier(Supplier<SchoolDate> schoolDateSupplier) {
        this.schoolDateSupplier = schoolDateSupplier;
    }

    public void update(SchoolDate schoolDate) {
        store.set(storeKey, schoolDate);
    }

    public SchoolDate get() {
        if (!redisTemplate.hasKey(storeKey) && schoolDateSupplier != null) {
            store.set(storeKey, schoolDateSupplier.get());
            redisTemplate.expire(storeKey, 1, TimeUnit.MINUTES);
        }
        return store.get(storeKey);
    }
}
