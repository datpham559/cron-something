package com.example.cron.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperInstance {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String ObjectToJson(Object object) {
        String result = null;
        ObjectWriter ow = OBJECT_MAPPER.registerModule(new JavaTimeModule())
                .enable(new JsonGenerator.Feature[]{JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN})
                .writer().withDefaultPrettyPrinter();
        try {
            result = ow.writeValueAsString(object);
        } catch (JsonProcessingException var4) {
            var4.printStackTrace();
        }
        return result;
    }
}
