package com.senacor.reactile.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.user.User;
import com.senacor.reactile.user.UserId;
import io.vertx.core.json.JsonObject;

public class CustomerAddressChangedEvt implements Event<CustomerId> {
    private final CustomerId id;
    private final UserId userId;
    private final Address newAddress;

    public CustomerAddressChangedEvt(
            UserId userId,
            CustomerId id,
            Address newAddress) {
        this.userId = userId;
        this.id = id;
        this.newAddress = newAddress;
    }

    public UserId getUserId() {
        return userId;
    }

    public Address getNewAddress() {
        return newAddress;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("userId", userId.getId())
                .put("id", id.getId())
                .put("address", newAddress.toJson());
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
}
