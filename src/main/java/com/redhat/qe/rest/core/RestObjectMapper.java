package com.redhat.qe.rest.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */
public class RestObjectMapper extends ObjectMapper {

    /**  */
    private static final long serialVersionUID = -1702600618371852994L;

    public RestObjectMapper() {
        config(this);
    }

    public ObjectMapper config(ObjectMapper mapper) {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}