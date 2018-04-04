package com.redhat.qe.rest.core.typeresolvers;

import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.qe.rest.core.RestObjectMapper;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class MapJavaTypeResolver {

    private final ObjectMapper objectMapper;

    public MapJavaTypeResolver() {
        this.objectMapper = new RestObjectMapper();
    }

    /**
     * Map with Generic Key and Value, i.e.: Map<String, TaggedBucketPoint>
     */
    public JavaType get(@SuppressWarnings("rawtypes") Class<? extends Map> mapClazz, Class<?> mapClazzKey,
            Class<?> mapClazzValue) {
        JavaType mapClazzKeyType = objectMapper.getTypeFactory().constructType(mapClazzKey);
        JavaType mapClazzValueType = objectMapper.getTypeFactory().constructType(mapClazzValue);

        return objectMapper.getTypeFactory().constructMapType(mapClazz, mapClazzKeyType, mapClazzValueType);
    }

    /**
     * Map with Generic Key and Genric Value, i.e.: Map<String, List<TaggedBucketPoint>>
     */
    public JavaType get(@SuppressWarnings("rawtypes") Class<? extends Map> mapClazz, Class<?> mapClazzKey,
            Class<?> mapClazzValue, Class<?> mapClazzParametrizedValue) {
        JavaType mapClazzKeyType = objectMapper.getTypeFactory().constructType(mapClazzKey);
        JavaType parametrizedClazzType = objectMapper.getTypeFactory().constructParametrizedType(mapClazzValue,
                mapClazzValue, mapClazzParametrizedValue);

        return objectMapper.getTypeFactory().constructMapType(mapClazz, mapClazzKeyType, parametrizedClazzType);
    }
}