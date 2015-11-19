package com.senacor.reactile.service.appointment;

import com.senacor.reactile.domain.Identity;
import com.senacor.reactile.json.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;

import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshalZonedDateTime;

/**
 * @author Andreas Keefer
 */
@DataObject
public class Appointment implements Jsonizable ,Identity<AppointmentId> {

    private final AppointmentId id;
    private final String name;
    private final String customerId;
    private final String branchId;
    private final ZonedDateTime start;
    private final ZonedDateTime end;
    private final String note;
    private final String userId;

    public Appointment() {
        this(Appointment.newBuilder());
    }

    public Appointment(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public Appointment(Appointment appointment) {
        this(Appointment.newBuilder(appointment));
    }

    private Appointment(Builder builder) {
        id = builder.id;
        name = builder.name;
        customerId = builder.customerId;
        branchId = builder.branchId;
        start = builder.start;
        end = builder.end;
        note = builder.note;
        userId = builder.userId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Appointment copy) {
        Builder builder = new Builder();
        builder.id = copy.id;
        builder.name = copy.name;
        builder.customerId = copy.customerId;
        builder.branchId = copy.branchId;
        builder.start = copy.start;
        builder.end = copy.end;
        builder.note = copy.note;
        builder.userId = copy.userId;
        return builder;
    }

    public AppointmentId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getBranchId() {
        return branchId;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public String getNote() {
        return note;
    }

    public String getUserId() {
        return userId;
    }

    public static Appointment fromJson(JsonObject jsonObject) {
        return null == jsonObject ? null : Appointment.newBuilder()
                .withId(jsonObject.getString("id"))
                .withName(jsonObject.getString("name"))
                .withCustomerId(jsonObject.getString("customerId"))
                .withBranchId(jsonObject.getString("branchId"))
                .withStart(unmarshalZonedDateTime(jsonObject.getString("start")))
                .withEnd(unmarshalZonedDateTime(jsonObject.getString("end")))
                .withNote(jsonObject.getString("note"))
                .withUserId(jsonObject.getString("userId"))
                .build();
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.getId())
                .put("name", name)
                .put("customerId", customerId)
                .put("branchId", branchId)
                .put("start", marshal(start))
                .put("end", marshal(end))
                .put("note", note)
                .put("userId", userId);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static final class Builder {
        private AppointmentId id;
        private String name;
        private String customerId;
        private String branchId;
        private ZonedDateTime start;
        private ZonedDateTime end;
        private String note;
        private String userId;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = new AppointmentId(id);
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withBranchId(String branchId) {
            this.branchId = branchId;
            return this;
        }

        public Builder withStart(ZonedDateTime start) {
            this.start = start;
            return this;
        }

        public Builder withEnd(ZonedDateTime end) {
            this.end = end;
            return this;
        }

        public Builder withNote(String note) {
            this.note = note;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Appointment build() {
            return new Appointment(this);
        }
    }
}
