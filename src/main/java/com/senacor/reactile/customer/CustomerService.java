package com.senacor.reactile.customer;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

@ProxyGen
public interface CustomerService {

    static final String ADDRESS = "CustomerService";

    void getCustomer(CustomerId customerId, Handler<AsyncResult<Customer>> resultHandler);

    void createCustomer(Customer customer, Handler<AsyncResult<Customer>> resultHandler);

    void updateAddress(CustomerId customerId, Address address, Handler<AsyncResult<Customer>> resultHandler);

    void updateContact(CustomerId customerId, Contact address, Handler<AsyncResult<Customer>> resultHandler);

    @GenIgnore
    default Observable<Customer> getCustomer(CustomerId customerId) {
        ObservableFuture<Customer> observableFuture = RxHelper.observableFuture();
        getCustomer(customerId, observableFuture.toHandler());
        return observableFuture;
    }

    @GenIgnore
    default Observable<Customer> createCustomer(Customer customer) {
        ObservableFuture<Customer> observableFuture = RxHelper.observableFuture();
        createCustomer(customer, observableFuture.toHandler());
        return observableFuture;
    }

    @GenIgnore
    default Observable<Customer> updateAddress(CustomerId customerId, Address address) {
        ObservableFuture<Customer> observableFuture = RxHelper.observableFuture();
        updateAddress(customerId, address, observableFuture.toHandler());
        return observableFuture;
    }

    @GenIgnore
    default Observable<Customer> updateContact(CustomerId customerId, Contact contact) {
        ObservableFuture<Customer> observableFuture = RxHelper.observableFuture();
        updateContact(customerId, contact, observableFuture.toHandler());
        return observableFuture;
    }
}
