package com.senacor.reactile.customer;

import io.vertx.core.json.JsonObject;

public class Contact {
    public JsonObject toJson() {
        return new JsonObject();
    }

    public static Contact fromJson(JsonObject jsonObject) {
        return new Contact();
    }
}
