package com.senacor.reactile;

import com.senacor.reactile.domain.Jsonizable;
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
