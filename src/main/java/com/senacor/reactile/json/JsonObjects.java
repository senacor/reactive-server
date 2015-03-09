package com.senacor.reactile.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public final class JsonObjects {
    private JsonObjects() {
    }

    public static <T> List<T> unmarshal(JsonArray jsonArray, Function<JsonObject, T> mapping) {
        return jsonArray.stream()
                .map(object -> (JsonObject) object)
                .map(mapping)
                .collect(toList());
    }

    public static <T> JsonArray marshal(List<T> objects, Function<T, JsonObject> mapping) {
        return new JsonArray(objects.stream().map(mapping).collect(toList()));
    }
}
