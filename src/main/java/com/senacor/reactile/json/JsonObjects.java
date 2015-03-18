package com.senacor.reactile.json;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public final class JsonObjects {
    private JsonObjects() {
    }

    public static <T> List<T> unmarshal(JsonArray jsonArray, Function<JsonObject, T> mapping) {
        return jsonArray.stream()
                .map(object -> object instanceof LinkedHashMap ? new JsonObject((Map<String, Object>) object) : (JsonObject) object)
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
}
