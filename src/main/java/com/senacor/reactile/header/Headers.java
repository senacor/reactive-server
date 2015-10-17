package com.senacor.reactile.header;

import io.vertx.core.eventbus.DeliveryOptions;

public class Headers {

    public static DeliveryOptions action(String action) {
        return new DeliveryOptions().addHeader("action", action);
    }
}
