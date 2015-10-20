package com.senacor.reactile.service.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.domain.IdObject;
import com.senacor.reactile.service.creditcard.CreditCardId;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@DataObject
public class UserId implements IdObject {

    private final String id;

    public UserId(@JsonProperty("id") String id) {
        checkArgument(id != null);
        this.id = id;
    }

    public UserId() {
        this((String) null);
    }

    public UserId(UserId userId) {
        this(userId.getId());
    }

    public UserId(JsonObject jsonObject) {
        this(jsonObject.getString("id"));
    }

    public UserId(CreditCardId creditCardId) {
        this(creditCardId.getId());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject().put("id", toValue());
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
        final UserId other = (UserId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "UserId{" +
                "id='" + id + '\'' +
                '}';
    }

}
