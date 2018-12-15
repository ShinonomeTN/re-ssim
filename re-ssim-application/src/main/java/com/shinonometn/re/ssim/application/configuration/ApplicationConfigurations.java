package com.shinonometn.re.ssim.application.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.shinonometn.re.ssim.service.commons.InMemoryStore;
import com.shinonometn.re.ssim.service.commons.InMemoryStoreManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Configuration
public class ApplicationConfigurations {

    @Bean
    public TaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    /*
     *
     * Jackson serializer
     *
     * */
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
//        filterProvider.addFilter("classId", SimpleBeanPropertyFilter.SerializeExceptFilter.filterOutAllExcept("@class"));
//        objectMapper.setFilterProvider(filterProvider);
//
//        return objectMapper;
//    }

    /*
     *
     * Set Jackson as default redis template serializer
     *
     * */
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        RedisSerializer<String> keySerializer = new StringRedisSerializer();
//        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer();
//
//        template.setDefaultSerializer(valueSerializer);
//
//        template.setKeySerializer(keySerializer);
//        template.setHashKeySerializer(keySerializer);
//
//        template.setValueSerializer(valueSerializer);
//
//        return template;
//    }

    /*
     *
     * Tell spring session use Jackson as serializer
     *
     * See https://docs.spring.io/spring-session/docs/current-SNAPSHOT/reference/html5/#custom-redisserializer
     *
     * */
//    @Bean
//    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
//        return new GenericJackson2JsonRedisSerializer();
//    }

    @Bean
    public InMemoryStoreManager inMemoryStoreManager(List<InMemoryStore> storeList) {
        return new InMemoryStoreManager(storeList);
    }
}
