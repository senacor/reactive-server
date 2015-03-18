package com.senacor.reactile.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public final class JsonObjects {
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

    private JsonObjects() {
    }

    public static <T> List<T> unmarshal(JsonArray jsonArray, Function<JsonObject, T> mapping) {
        return jsonArray.stream()
                .map(object -> object instanceof Map ? new JsonObject((Map<String, Object>) object) : (JsonObject) object)
                .map(mapping)
                .collect(toList());
    }

    public static <T> JsonArray marshal(List<T> objects, Function<T, JsonObject> mapping) {
        return new JsonArray(objects.stream().map(mapping).collect(toList()));
    }

    public static JsonObject toJson(Jsonizable jsonizable) {
        return jsonizable.toJson();
    }
    public static JsonArray toJsonArray(List<? extends Jsonizable> jsonizables) {
        return new JsonArray(jsonizables.stream().map(JsonObjects::toJson).collect(toList()));
    }

    public static JsonObject $() {
        return new JsonObject();
    }

    public static JsonObject arr() {
        return new JsonObject();
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
