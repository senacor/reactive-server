package com.senacor.reactile.customer;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface CustomerService {

    static final String ADDRESS = "CustomerService";

    static final String ADDRESS_EVENT_UPDATE_ADDRESS = CustomerService.ADDRESS + "#updateAddress";

    void getCustomer(CustomerId customerId, Handler<AsyncResult<Customer>> resultHandler);

    void createCustomer(Customer customer, Handler<AsyncResult<Customer>> resultHandler);

    void updateAddress(CustomerId customerId, Address address, Handler<AsyncResult<Customer>> resultHandler);

    void updateContact(CustomerId customerId, Contact address, Handler<AsyncResult<Customer>> resultHandler);

}
