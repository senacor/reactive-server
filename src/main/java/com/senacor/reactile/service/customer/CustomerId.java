package com.senacor.reactile.service.customer;

import com.senacor.reactile.domain.IdObject;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@DataObject
public class CustomerId implements IdObject {

    private final String id;

    public CustomerId() {
        this((String)null);
    }

    public CustomerId(String id) {
        checkArgument(id != null);
        this.id = id;
    }

    public CustomerId(CustomerId customerId) {
        this(customerId.getId());
    }

    public CustomerId(JsonObject jsonObject) {
        this(jsonObject.getString("id"));
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
        final CustomerId other = (CustomerId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "CustomerId{" +
                "id='" + id + '\'' +
                '}';
    }

}
