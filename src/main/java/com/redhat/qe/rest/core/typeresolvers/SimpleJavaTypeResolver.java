package com.redhat.qe.rest.core.typeresolvers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.qe.rest.core.RestObjectMapper;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */
public class SimpleJavaTypeResolver {

    private final ObjectMapper objectMapper;

    public SimpleJavaTypeResolver() {
        this.objectMapper = new RestObjectMapper();
    }

    /**
     * Simple Class, i.e.: Double
     */
    public JavaType get(Class<?> clazz) {
        return objectMapper.getTypeFactory().constructType(clazz);
    }

    /**
     * Simple Class with Generic, i.e.: Metric<Double>
     */
    public JavaType get(Class<?> clazz, Class<?> parametrizedClazz) {
        JavaType parametrizedClazzType = objectMapper.getTypeFactory().constructType(parametrizedClazz);

        return objectMapper.getTypeFactory().constructParametrizedType(clazz, clazz, parametrizedClazzType);
    }
}