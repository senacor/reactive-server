package com.senacor.reactile.service.appointment;

import com.senacor.reactile.domain.IdObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class AppointmentId implements IdObject {

    private final String id;

    public AppointmentId() {
        this((String)null);
    }

    public AppointmentId(String id) {
        this.id = id;
    }

    public AppointmentId(long id) {
        this.id = String.valueOf(id);
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
        final AppointmentId other = (AppointmentId) obj;
        return Objects.equals(this.id, other.id);
    }
}
