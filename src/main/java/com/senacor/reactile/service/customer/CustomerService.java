package com.senacor.reactile.service.customer;

import com.senacor.reactile.abstractservice.Action;
import rx.Observable;

public interface CustomerService {

    String ADDRESS_EVENT_UPDATE_ADDRESS = "CustomerService#updateAddress";

    @Action(returnType = Customer.class)
    public Observable<Customer> getCustomer(CustomerId customerId);

    @Action(returnType = Customer.class)
    public Observable<Customer> createCustomer(Customer customer);

    @Action(returnType = Customer.class)
    public Observable<Customer> updateAddress(CustomerId customerId, Address address);

    @Action(returnType = Customer.class)
    public Observable<Customer> updateContact(CustomerId customerId, Contact address);

}
