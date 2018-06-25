package com.shinonometn.re.ssim.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CacheService {

    private final RedisTemplate<String,String> redis;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CacheService(RedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public void put(String key, Object object){
        try {
            redis.opsForValue().set(key,objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T get(String key, TypeReference<T> typeReference){

        if(!redis.hasKey(key)) return null;

        String s = redis.opsForValue().get(key);

        try {
            return objectMapper.readValue(s,typeReference);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void put(String key, long expire, Object object){
        try {
            redis.opsForValue().set(key,objectMapper.writeValueAsString(object),expire);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public void expire(String key){
        redis.delete(key);
    }
}
