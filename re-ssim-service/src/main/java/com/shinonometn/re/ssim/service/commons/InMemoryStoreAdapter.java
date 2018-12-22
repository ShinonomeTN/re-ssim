package com.shinonometn.re.ssim.service.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class InMemoryStoreAdapter<T> implements InMemoryStore {

    // Default empty array
    private final static byte[] EMPTY_ARRAY = new byte[0];

    // All children user a same redis key serializer
    private final static RedisSerializer<String> keySerializer = new StringRedisSerializer();

    // All children use a same objectMapper
    private final static ObjectMapper objectMapper = new ObjectMapper();

    // Key prefix for identify keys that storage to use
    @Value("${app.inMemoryDataKey:app.store}")
    private String keyPrefix = "app.store";

    // Domain name for split out different storage
    private final String domain;

    // Full key name for access redis
    protected final String storeKey;

    // Redis operator
    protected final RedisTemplate<String, T> redisTemplate;

    @SuppressWarnings("unchecked")
    protected InMemoryStoreAdapter(RedisConnectionFactory redisConnectionFactory, String domain) {
        this.redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        Type mainType = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) mainType;
        Class<T> mainTypeClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];

        // Every children has their own valueSerializer
        RedisSerializer<T> valueSerializer = new RedisSerializer<T>() {
            private final ObjectReader objectReader = objectMapper.readerFor(mainTypeClass);
            private final ObjectWriter objectWriter = objectMapper.writer();

            @NotNull
            @Override
            public byte[] serialize(@Nullable T t) throws SerializationException {
                try {
                    return t == null ? EMPTY_ARRAY : objectWriter.writeValueAsBytes(t);
                } catch (JsonProcessingException e) {
                    throw new SerializationException("Could not write object to JSON: ", e);
                }
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public T deserialize(@Nullable byte[] bytes) throws SerializationException {
                try {
                    return (bytes == null || bytes.length <= 0) ? null : objectReader.readValue(bytes);
                } catch (IOException e) {
                    throw new SerializationException("Could not read object from JSON: ", e);
                }
            }
        };

        // Setting serializers
        redisTemplate.setDefaultSerializer(keySerializer);

        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        redisTemplate.afterPropertiesSet();

        this.domain = domain;
        this.storeKey = keyPrefix + "." + domain;
    }

    @Override
    public final String domain() {
        return domain;
    }

    @Override
    public void clear() {
        redisTemplate.delete(storeKey);
    }

}
