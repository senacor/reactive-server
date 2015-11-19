package com.senacor.reactile.service.appointment;

import com.google.common.base.MoreObjects;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.json.Jsonizable;

import io.vertx.core.json.JsonObject;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class AppointmentCreatedOrUpdatedEvent implements Event<String>, Jsonizable {

    private final String id;
    private final Appointment appointment;

    public AppointmentCreatedOrUpdatedEvent() {
        this(null, null);
    }

    public AppointmentCreatedOrUpdatedEvent(AppointmentCreatedOrUpdatedEvent event) {
        this(event.id, event.appointment);
    }

    public AppointmentCreatedOrUpdatedEvent(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public AppointmentCreatedOrUpdatedEvent(String id, Appointment appointment) {
        this.id = id;
        this.appointment = appointment;
    }

    private AppointmentCreatedOrUpdatedEvent(Builder builder) {
        this(builder.id, builder.appointment);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public JsonObject toJson() {
        return new JsonObject() //
            .put("id", id) //
            .put("appointment", appointment == null ? null : appointment.toJson());
    }

    public static AppointmentCreatedOrUpdatedEvent fromJson(JsonObject json) {
        return newBuilder().withId(json.getString("id")).build();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).toString();
    }

    public static final class Builder {
        private String id;
        private Appointment appointment;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withAppointment(Appointment appointment) {
            this.appointment = appointment;
            return this;
        }

        public AppointmentCreatedOrUpdatedEvent build() {
            return new AppointmentCreatedOrUpdatedEvent(this);
        }
    }
}
