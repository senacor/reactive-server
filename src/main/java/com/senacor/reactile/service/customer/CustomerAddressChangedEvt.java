package com.senacor.reactile.service.customer;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.senacor.reactile.domain.Jsonizable;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.service.user.User;
import com.senacor.reactile.service.user.UserId;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@DataObject
public class CustomerAddressChangedEvt implements Event<CustomerId>, Jsonizable {
    private final CustomerId id;
    private final Address newAddress;

    public CustomerAddressChangedEvt() {
        this(null, null);
    }

    public CustomerAddressChangedEvt(CustomerAddressChangedEvt event) {
        this(event.id, event.newAddress);
    }

    public CustomerAddressChangedEvt(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public CustomerAddressChangedEvt(
            CustomerId id,
            Address newAddress) {
        this.id = id;
        this.newAddress = newAddress;
    }

    private CustomerAddressChangedEvt(Builder builder) {
        this(builder.id, builder.newAddress);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Address getNewAddress() {
        return newAddress;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", null == id ? null : id.getId())
                .put("address", null == newAddress ? null : newAddress.toJson());
    }

    public static CustomerAddressChangedEvt fromJson(JsonObject json) {
        return newBuilder()
                .withId(new CustomerId(json.getString("id")))
                .withNewAddress(Address.fromJson(json.getJsonObject("address")))
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
                .add("newAddress", newAddress)
                .toString();
    }


    public static final class Builder {
        private CustomerId id;
        private UserId userId;
        private Address newAddress;

        private Builder() {
        }

        public Builder withId(CustomerId id) {
            this.id = id;
            return this;
        }

        public Builder withNewAddress(Address newAddress) {
            this.newAddress = newAddress;
            return this;
        }

        public CustomerAddressChangedEvt build() {
            return new CustomerAddressChangedEvt(this);
        }
    }
}
