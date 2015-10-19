package com.senacor.reactile.service.customer;

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
    // TODO (ak) die property wuerde ich entfernen
    private final UserId userId;
    private final Address newAddress;

    public CustomerAddressChangedEvt() {
        this(null, null, null);
    }

    public CustomerAddressChangedEvt(CustomerAddressChangedEvt event) {
        this(event.userId, event.id, event.newAddress);
    }

    public CustomerAddressChangedEvt(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public CustomerAddressChangedEvt(
            UserId userId,
            CustomerId id,
            Address newAddress) {
        this.userId = userId;
        this.id = id;
        this.newAddress = newAddress;
    }

    private CustomerAddressChangedEvt(Builder builder) {
        this(builder.userId, builder.id, builder.newAddress);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public UserId getUserId() {
        return userId;
    }

    public Address getNewAddress() {
        return newAddress;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("userId", null == userId ? null : userId.getId())
                .put("id", null == id ? null : id.getId())
                .put("address", null == newAddress ? null : newAddress.toJson());
    }

    public static CustomerAddressChangedEvt fromJson(JsonObject json) {
        String userId = json.getString("userId");
        return newBuilder()
                .withId(new CustomerId(json.getString("id")))
                .withUserId(null == userId ? null : new UserId(userId))
                .withNewAddress(Address.fromJson(json.getJsonObject("address")))
                .build();
    }

    // TODO (ak) sollte man sowas machen?!?
    public JsonObject replaceUser(User user) {
        JsonObject res = toJson().put("user", user.toJson());
        res.remove("userId");
        return res;
    }

    @Override
    public CustomerId getId() {
        return id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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

        public Builder withUserId(UserId userId) {
            this.userId = userId;
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
