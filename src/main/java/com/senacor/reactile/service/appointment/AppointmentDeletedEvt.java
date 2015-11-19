package com.senacor.reactile.service.appointment;

import com.google.common.base.MoreObjects;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.json.Jsonizable;

import io.vertx.core.json.JsonObject;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class AppointmentDeletedEvt implements Event<String>, Jsonizable {

    private final String id;

    public AppointmentDeletedEvt() {
        this((String) null);
    }

    public AppointmentDeletedEvt(AppointmentDeletedEvt event) {
        this(event.id);
    }

    public AppointmentDeletedEvt(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public AppointmentDeletedEvt(String id) {
        this.id = id;
    }

    private AppointmentDeletedEvt(Builder builder) {
        this(builder.id);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public JsonObject toJson() {
        return new JsonObject().put("id", id);
    }

    public static AppointmentDeletedEvt fromJson(JsonObject json) {
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

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public AppointmentDeletedEvt build() {
            return new AppointmentDeletedEvt(this);
        }
    }

}
