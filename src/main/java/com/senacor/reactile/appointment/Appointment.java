package com.senacor.reactile.appointment;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshalZonedDateTime;

/**
 * @author Andreas Keefer
 */
@DataObject
public class Appointment implements Jsonizable {

    private final String id;
    private final String name;
    private final String customerId;
    private final String branchId;
    private final ZonedDateTime start;
    private final ZonedDateTime end;
    private final String note;

    public Appointment(){
        this(null, null, null, null, null, null, null);
    }

    public Appointment(String id,
                       String name,
                       String customerId,
                       String branchId,
                       ZonedDateTime start,
                       ZonedDateTime end,
                       String note) {
        this.id = id;
        this.name = name;
        this.customerId = customerId;
        this.branchId = branchId;
        this.start = start;
        this.end = end;
        this.note = note;
    }

    public Appointment(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public Appointment(Appointment appointment) {
        this(appointment.getId(),
                appointment.getName(),
                appointment.getCustomerId(),
                appointment.getBranchId(),
                appointment.getStart(),
                appointment.getEnd(),
                appointment.getNote());
    }

    private Appointment(Builder builder) {
        id = builder.id;
        name = builder.name;
        customerId = builder.customerId;
        branchId = builder.branchId;
        start = builder.start;
        end = builder.end;
        note = builder.note;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getId() {
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

    public static Appointment fromJson(JsonObject jsonObject) {
        return null == jsonObject ? null : Appointment.newBuilder()
                .withId(jsonObject.getString("id"))
                .withName(jsonObject.getString("name"))
                .withCustomerId(jsonObject.getString("customerId"))
                .withBranchId(jsonObject.getString("branchId"))
                .withStart(unmarshalZonedDateTime(jsonObject.getString("start")))
                .withEnd(unmarshalZonedDateTime(jsonObject.getString("end")))
                .withNote(jsonObject.getString("note"))
                .build();
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("name", name)
                .put("customerId", customerId)
                .put("branchId", branchId)
                .put("start", marshal(start))
                .put("end", marshal(end))
                .put("note", note);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static final class Builder {
        private String id;
        private String name;
        private String customerId;
        private String branchId;
        private ZonedDateTime start;
        private ZonedDateTime end;
        private String note;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
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

        public Appointment build() {
            return new Appointment(this);
        }
    }
}
