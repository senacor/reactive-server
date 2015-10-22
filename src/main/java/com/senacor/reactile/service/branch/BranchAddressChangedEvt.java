package com.senacor.reactile.service.branch;

import com.google.common.base.MoreObjects;
import com.senacor.reactile.domain.Jsonizable;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.user.UserId;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class BranchAddressChangedEvt implements Event<String>, Jsonizable {
    private final String id;
    private final Address newAddress;

    public BranchAddressChangedEvt() {
        this(null, null);
    }

    public BranchAddressChangedEvt(BranchAddressChangedEvt event) {
        this(event.id, event.newAddress);
    }

    public BranchAddressChangedEvt(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public BranchAddressChangedEvt(
            String id,
            Address newAddress) {
        this.id = id;
        this.newAddress = newAddress;
    }

    private BranchAddressChangedEvt(Builder builder) {
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
                .put("id", null == id ? null : id)
                .put("address", null == newAddress ? null : newAddress.toJson());
    }

    public static BranchAddressChangedEvt fromJson(JsonObject json) {
        return newBuilder()
                .withId(json.getString("id"))
                .withNewAddress(Address.fromJson(json.getJsonObject("address")))
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
                .add("newAddress", newAddress)
                .toString();
    }


    public static final class Builder {
        private String id;
        private UserId userId;
        private Address newAddress;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withNewAddress(Address newAddress) {
            this.newAddress = newAddress;
            return this;
        }

        public BranchAddressChangedEvt build() {
            return new BranchAddressChangedEvt(this);
        }
    }
}
