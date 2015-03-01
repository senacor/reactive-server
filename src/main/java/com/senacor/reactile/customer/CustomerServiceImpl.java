package com.senacor.reactile.customer;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class CustomerServiceImpl implements CustomerService {
    private final Vertx vertx;

    public CustomerServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<Customer> getCustomer(CustomerId customerId) {
        return vertx.eventBus()
                .<Customer>sendObservable(CustomerServiceVerticle.ADDRESS, new CustomerId(customerId.getId()), action("getCustomer"))
                .map(Message::body);
    }


    private static DeliveryOptions action(String action) {
        return new DeliveryOptions().addHeader("action", action);
    }
}
