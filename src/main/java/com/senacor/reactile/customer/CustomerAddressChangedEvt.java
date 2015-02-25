package com.senacor.reactile.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.auth.User;
import com.senacor.reactile.auth.UserId;
import io.vertx.core.json.JsonObject;

public class CustomerAddressChangedEvt {
    private final UserId userId;
    private final CustomerId customerId;
    private final Address newAddress;

    public CustomerAddressChangedEvt(
            @JsonProperty("userId") UserId userId,
            @JsonProperty("customerId") CustomerId customerId,
            @JsonProperty("newAddress") Address newAddress) {
        this.userId = userId;
        this.customerId = customerId;
        this.newAddress = newAddress;
    }

    public UserId getUserId() {
        return userId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Address getNewAddress() {
        return newAddress;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("userId", userId.getId())
                .put("customerId", customerId.getId())
                .put("address", newAddress.toJson());
    }

    public JsonObject replaceUser(User user) {
        JsonObject res = toJson().put("user", user.toJson());
        res.remove("userId");
        return res;
    }
}
