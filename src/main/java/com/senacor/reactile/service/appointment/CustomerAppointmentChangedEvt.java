package com.senacor.reactile.service.appointment;

import com.google.common.base.MoreObjects;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.json.Jsonizable;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class CustomerAppointmentChangedEvt implements Event<CustomerId>, Jsonizable {
    private final CustomerId id;
    private final Appointment appointment;

    public CustomerAppointmentChangedEvt() {
        this(null, null);
    }

    public CustomerAppointmentChangedEvt(CustomerAppointmentChangedEvt event) {
        this(event.id, event.appointment);
    }

    public CustomerAppointmentChangedEvt(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public CustomerAppointmentChangedEvt(
            CustomerId id,
            Appointment appointment) {
        this.id = id;
        this.appointment = appointment;
    }

    private CustomerAppointmentChangedEvt(Builder builder) {
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
                .put("id", null == id ? null : id.getId())
                .put("appointment", null == appointment ? null : appointment.toJson());
    }

    public static CustomerAppointmentChangedEvt fromJson(JsonObject json) {
        return newBuilder()
                .withId(new CustomerId(json.getString("id")))
                .withAppointment(Appointment.fromJson(json.getJsonObject("appointment")))
                .build();
    }

    @Override
    public CustomerId getId() {
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
        private CustomerId id;
        private Appointment appointment;

        private Builder() {
        }

        public Builder withCustomerId(String id) {
            this.id = new CustomerId(id);
            return this;
        }

        public Builder withId(CustomerId id) {
            this.id = id;
            return this;
        }

        public Builder withAppointment(Appointment newAddress) {
            this.appointment = newAddress;
            return this;
        }

        public CustomerAppointmentChangedEvt build() {
            return new CustomerAppointmentChangedEvt(this);
        }
    }
}
