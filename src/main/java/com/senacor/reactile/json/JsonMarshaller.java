package com.senacor.reactile.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;

public class JsonMarshaller {
    private final static ObjectMapper om = new ObjectMapper();

    static {
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        om.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        om.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        //allow objects without properties
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static byte[] toJson(Object object) {
        try {
            return om.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject toJsonObject(Object object) {
        return new JsonObject(toJsonString(object));
    }

    public static String toJsonString(Object object) {
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Buffer toBuffer(Object object) {
        return Buffer.buffer(toJsonString(object));
    }
}
