package com.senacor.reactile.service.appointment;

import com.senacor.reactile.domain.IdObject;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@DataObject
public class AppointmentId implements IdObject {

    private final String id;

    public AppointmentId() {
        this((String)null);
    }

    public AppointmentId(String id) {
        checkArgument(id != null);
        this.id = id;
    }

    public AppointmentId(AppointmentId customerId) {
        this(customerId.getId());
    }

    public AppointmentId(JsonObject jsonObject) {
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
        final AppointmentId other = (AppointmentId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "AppointmentId{" +
                "id='" + id + '\'' +
                '}';
    }

}
