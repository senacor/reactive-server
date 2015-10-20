package com.senacor.reactile.service.creditcard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.domain.IdObject;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject
public class CreditCardId implements IdObject {

    private final String id;

    public CreditCardId() {
        this((String)null);
    }

    public CreditCardId(@JsonProperty("id") String id) {
        this.id = id;
    }

    public CreditCardId(JsonObject jsonObject) {
        this(jsonObject.getString("id"));
    }

    public CreditCardId(CreditCardId creditCardId) {
        this(creditCardId.getId());
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject().put("id", toValue());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CreditCardId other = (CreditCardId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "CreditCardId{" +
                "id='" + id + '\'' +
                '}';
    }
}
