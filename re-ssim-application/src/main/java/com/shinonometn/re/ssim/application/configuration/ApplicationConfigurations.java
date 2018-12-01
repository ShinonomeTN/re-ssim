package com.shinonometn.re.ssim.application.configuration;

import com.shinonometn.re.ssim.service.commons.InMemoryStore;
import com.shinonometn.re.ssim.service.commons.InMemoryStoreManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Configuration
public class ApplicationConfigurations {

    @Bean
    public TaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public InMemoryStoreManager inMemoryStoreManager(List<InMemoryStore> storeList){
        return new InMemoryStoreManager(storeList);
    }
}
