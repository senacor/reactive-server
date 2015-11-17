package com.senacor.reactile.domain;

import com.senacor.reactile.json.Jsonizable;
import io.vertx.core.json.JsonObject;

public interface IdObject extends ValueObject, Jsonizable {

    String getId();

    default String toValue() {
        return getId();
    }

    @Override
    default JsonObject toJson() {
        return new JsonObject().put("id", toValue());
    }
}
