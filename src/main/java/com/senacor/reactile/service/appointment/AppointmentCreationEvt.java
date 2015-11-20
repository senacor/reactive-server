package com.senacor.reactile.service.appointment;

import com.google.common.base.MoreObjects;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.json.Jsonizable;
import com.senacor.reactile.service.user.UserId;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class AppointmentCreationEvt implements Event<UserId>, Jsonizable {
    private final Appointment appointment;
    private UserId userId;

    public AppointmentCreationEvt() {
        this(null, null);
    }

    public AppointmentCreationEvt(AppointmentCreationEvt event) {
        this(event.appointment, event.userId);
    }

    public AppointmentCreationEvt(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public AppointmentCreationEvt(Appointment appointment, UserId userId) {
        this.appointment = appointment;
        this.userId = userId;
    }

    private AppointmentCreationEvt(Builder builder) {
        this(builder.appointment, builder.userId);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("appointment", null == appointment ? null : appointment.toJson())
                .put("id", null == userId ? null : userId.toJson());
    }

    public static AppointmentCreationEvt fromJson(JsonObject json) {
        return newBuilder()
                .withAppointment(Appointment.fromJson(json.getJsonObject("appointment")))
                .withUserId(new UserId(json.getJsonObject("id")))
                .build();
    }

    @Override
    public UserId getId() {
        return userId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("appointment", appointment)
                .add("userId", userId)
                .toString();
    }


    public static final class Builder {
        private Appointment appointment;
        private UserId userId;

        private Builder() {
        }

        public Builder withAppointment(Appointment appointment) {
            this.appointment = appointment;
            return this;
        }

        public Builder withUserId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public AppointmentCreationEvt build() {
            return new AppointmentCreationEvt(this);
        }
    }
}
