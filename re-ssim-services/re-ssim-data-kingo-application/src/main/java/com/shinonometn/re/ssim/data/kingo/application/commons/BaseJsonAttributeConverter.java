package com.shinonometn.re.ssim.data.kingo.application.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseJsonAttributeConverter<T> implements AttributeConverter<T, String> {

    private final static Logger logger = LoggerFactory.getLogger(BaseJsonAttributeConverter.class);

    private final ObjectReader objectReader;
    private final ObjectWriter objectWriter;
    private final JavaType type;

    public BaseJsonAttributeConverter() {

        Type genericType = this.getClass().getGenericSuperclass();
        if (genericType instanceof ParameterizedType)
            genericType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

        ObjectMapper objectMapper = new ObjectMapper();
        type = objectMapper.constructType(genericType);

        objectReader = objectMapper.readerFor(objectMapper.constructType(genericType));
        objectWriter = objectMapper.writerFor(type);

        logger.debug("converter[{}] created, Type bind to : {}", this.getClass(), genericType.getTypeName());
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        try {
            return attribute == null ? null : objectWriter.forType(type).writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Convert {} to String failed! {}", attribute.getClass(), e);
            return null;
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : objectReader.forType(type).readValue(dbData);
        } catch (IOException e) {
            logger.error("Convert string data to object failed! \n Raw json string : {} \n error : {}", dbData, e);
            return null;
        }
    }
}
