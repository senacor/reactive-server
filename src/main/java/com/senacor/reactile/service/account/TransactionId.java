package com.senacor.reactile.service.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.IdObject;
import com.senacor.reactile.service.creditcard.CreditCardId;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
@DataObject
public class TransactionId implements IdObject {
    private final String id;

    public TransactionId() {
        this((String)null);
    }

    public TransactionId(@JsonProperty("id") String id) {
        this.id = id;
    }

    public TransactionId(JsonObject jsonObject) {
        this(jsonObject.getString("id"));
    }

    public TransactionId(CreditCardId creditCardId) {
        this(creditCardId.getId());
    }

    public TransactionId(TransactionId transactionId) {
        this(transactionId.getId());
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
        final TransactionId other = (TransactionId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "TransactionId{" +
                "id='" + id + '\'' +
                '}';
    }

}
