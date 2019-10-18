package com.shinonometn.re.ssim.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class JSON {

    private final static ObjectMapper mapper = new ObjectMapper();

    private final static TypeReference<Map<String, Object>> objectMapTypeReference = new TypeReference<Map<String, Object>>() {
    };

    public static <T> T read(InputStream inputStream, Class<T> clazz) throws IOException {
        return mapper.readerFor(clazz).readValue(inputStream);
    }

    public static <T> T read(String json, TypeReference<T> typeReference) throws IOException {
        return mapper.readerFor(typeReference).readValue(json);
    }

    public static <T> T read(InputStream inputStream, TypeReference<T> typeReference) throws IOException {
        return mapper.readerFor(typeReference).readValue(inputStream);
    }

    public static Map<String, Object> readAsMap(String json) throws IOException {
        return mapper.readerFor(objectMapTypeReference).readValue(json);
    }

    public static Map<String, Object> readAsMap(InputStream inputStream) throws IOException {
        return mapper.readerFor(objectMapTypeReference).readValue(inputStream);
    }

    public static void write(OutputStream outputStream, Object object) throws IOException {
        mapper.writeValue(outputStream, object);
    }

    @NotNull
    public static String parse(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @Nullable
    public static byte[] writeToByte(@Nullable Object o) throws JsonProcessingException {
        return mapper.writeValueAsBytes(o);
    }

    @Nullable
    public static <T> T read(byte[] bytes, TypeReference<T> typeReference) throws IOException {
        return mapper.readerFor(typeReference).readValue(bytes);
    }
}
