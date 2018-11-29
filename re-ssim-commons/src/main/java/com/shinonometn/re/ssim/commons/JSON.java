package com.shinonometn.re.ssim.commons;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JSON {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static <T> T read(InputStream inputStream, Class<T> clazz) throws IOException {
        return mapper.readerFor(clazz).readValue(inputStream);
    }

    public static <T> T read(InputStream inputStream, TypeReference<T> typeReference) throws IOException {
        return mapper.readerFor(typeReference).readValue(inputStream);
    }

    public static void write(OutputStream outputStream, Object object) throws IOException {
        mapper.writeValue(outputStream,object);
    }

}
