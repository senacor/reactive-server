package com.senacor.reactile.customer;

import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import javax.inject.Inject;

import static com.senacor.reactile.header.Headers.action;

public class CustomerServiceImpl implements CustomerService {
    private final Vertx vertx;

    @Inject
    public CustomerServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<Customer> getCustomer(CustomerId customerId) {
        return vertx.eventBus()
                .<Customer>sendObservable(CustomerServiceVerticle.ADDRESS, new CustomerId(customerId.getId()), action("getCustomer"))
                .map(Message::body);
    }

    @Override
    public Observable<Customer> createCustomer(Customer customer) {
        return vertx.eventBus()
                .<Customer>sendObservable(CustomerServiceVerticle.ADDRESS, customer, action("add"))
                .map(Message::body);
    }


}
