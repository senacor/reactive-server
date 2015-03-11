package com.senacor.reactile.domain;

import io.vertx.core.json.JsonObject;

public interface Jsonizable {

    JsonObject toJson();
}
