package com.senacor.reactile.customer;

import io.vertx.core.json.JsonObject;

public class Country {

    private final String name;
    private final String code;

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public JsonObject toJson() {
        return new JsonObject().put("name", name).put("code", code);
    }

    public static Country fromJson(JsonObject jsonObject) {
        return new Country(jsonObject.getString("name"), jsonObject.getString("code"));
    }
}
