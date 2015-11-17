package com.senacor.reactile.json;

import io.vertx.core.json.JsonObject;

public interface Jsonizable {
    JsonObject toJson();

}
