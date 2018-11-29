package com.shinonometn.re.ssim.service.statistics.plugin;

import com.shinonometn.re.ssim.service.commons.InMemoryStoreManagement;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class VisitorCounterPlugin extends InMemoryStoreManagement {

    private final StringRedisTemplate template;

    public VisitorCounterPlugin(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    protected String domain() {
        return "visitor-counter";
    }

    public void increase() {
        final String key = cachePrefix();

        if (!template.hasKey(key)) {
            template.opsForValue().set(key, "1");
            return;
        }

        template.opsForValue().increment(key, 1);
    }

    public Long get() {
        return Long.valueOf(template.opsForValue().get(domain()));
    }

}
