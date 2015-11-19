package com.senacor.reactile.service.appointment;

import com.google.common.base.MoreObjects;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.json.Jsonizable;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.user.UserId;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class AppointmentCreatedOrUpdatedEvt implements Event<String>, Jsonizable {
    private final String id;
    private final Appointment appointment;

    public AppointmentCreatedOrUpdatedEvt() {
        this(null, null);
    }

    public AppointmentCreatedOrUpdatedEvt(AppointmentCreatedOrUpdatedEvt event) {
        this(event.id, event.appointment);
    }

    public AppointmentCreatedOrUpdatedEvt(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public AppointmentCreatedOrUpdatedEvt(
            String id,
            Appointment appointment) {
        this.id = id;
        this.appointment = appointment;
    }

    private AppointmentCreatedOrUpdatedEvt(Builder builder) {
        this(builder.id, builder.appointment);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", null == id ? null : id)
                .put("appointment", null == appointment ? null : appointment.toJson());
    }

    public static AppointmentCreatedOrUpdatedEvt fromJson(JsonObject json) {
        return newBuilder()
                .withId(json.getString("id"))
                .withAppointment(Appointment.fromJson(json.getJsonObject("appointment")))
                .build();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("appointment", appointment)
                .toString();
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

        public AppointmentCreatedOrUpdatedEvt build() {
            return new AppointmentCreatedOrUpdatedEvt(this);
        }
    }
}
