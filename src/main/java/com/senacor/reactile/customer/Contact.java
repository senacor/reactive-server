package com.senacor.reactile.customer;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.json.JsonObject;

public class Contact implements Jsonizable {
    public JsonObject toJson() {
        return new JsonObject();
    }

    public static Contact fromJson(JsonObject jsonObject) {
        return new Contact();
    }
}
