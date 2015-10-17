package com.senacor.reactile.appointment;

import com.google.common.collect.ImmutableList;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshal;

/**
 * @author mhaunolder
 */
@DataObject
public class AppointmentList implements Jsonizable {

    private final List<Appointment> appointmentList;

    public AppointmentList() {
        this.appointmentList = Collections.emptyList();
    }

    public AppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList == null ? Collections.emptyList() : ImmutableList.copyOf(appointmentList);
    }

    public AppointmentList(AppointmentList appointmentList) {
        this.appointmentList = ImmutableList.copyOf(appointmentList.getAppointmentList());
    }

    public AppointmentList(Builder builder) {
        this.appointmentList = ImmutableList.copyOf(builder.appointmentList);
    }

    public AppointmentList(JsonObject jsonObject) {
        this.appointmentList = fromJson(jsonObject).getAppointmentList();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public static AppointmentList fromJson(JsonObject jsonObject) {
        checkArgument(jsonObject != null);
        return AppointmentList.newBuilder()
                .withAppointments(unmarshal(jsonObject.getJsonArray("appointmentList"), Appointment::fromJson))
                .build();
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("appointmentList", marshal(appointmentList, Appointment::toJson));
    }

    public static final class Builder {
        private List<Appointment> appointmentList = new ArrayList<>();

        public Builder() {
        }

        public List<Appointment> getAppointmentList() {
            return appointmentList;
        }

        public Builder withAppointments(List<Appointment> appointmentList) {
            this.appointmentList = appointmentList;
            return this;
        }

        public AppointmentList build() {
            return new AppointmentList(appointmentList);
        }
    }
}
