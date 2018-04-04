package com.redhat.qe.rest.core.typeresolvers;

import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.qe.rest.core.RestObjectMapper;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class CollectionJavaTypeResolver {

    private final ObjectMapper objectMapper;

    public CollectionJavaTypeResolver() {
        this.objectMapper = new RestObjectMapper();
    }

    /**
     * List with Generic, i.e.: List<Double>
     */
    public JavaType get(@SuppressWarnings("rawtypes") Class<? extends List> collectionClazz, Class<?> clazz) {
        JavaType clazzType = objectMapper.getTypeFactory().constructType(clazz);
        return objectMapper.getTypeFactory().constructCollectionType(collectionClazz, clazzType);
    }

    /**
     * List with Generic, Generic, i.e.: List<Metric<Double>>
     */
    public JavaType get(@SuppressWarnings("rawtypes") Class<? extends List> collectionClazz, Class<?> clazz,
            Class<?> parametrizedClazz) {
        JavaType parametrizedClazzType = objectMapper.getTypeFactory().constructType(parametrizedClazz);
        JavaType clazzType = objectMapper.getTypeFactory().constructParametrizedType(clazz, clazz,
                parametrizedClazzType);

        return objectMapper.getTypeFactory().constructCollectionType(collectionClazz, clazzType);
    }

    /**
     * List with Generic Map, Generic, i.e.: List<Map<String, Object>>
     */
    public JavaType get(@SuppressWarnings("rawtypes") Class<? extends List> collectionClazz, Class<?> mapClazz,
            Class<?> keyClazz, Class<?> valueClazz) {
        JavaType clazzType = objectMapper.getTypeFactory().constructMapLikeType(mapClazz, keyClazz, valueClazz);
        return objectMapper.getTypeFactory().constructCollectionType(collectionClazz, clazzType);
    }
}