package com.senacor.reactile.customer;

import rx.Observable;

public interface CustomerService {

    Observable<Customer> getCustomer(CustomerId customerId);

    Observable<Customer> createCustomer(Customer customer);
}
