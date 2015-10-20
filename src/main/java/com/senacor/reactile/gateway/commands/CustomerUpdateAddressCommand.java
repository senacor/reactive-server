package com.senacor.reactile.gateway.commands;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.rxjava.service.customer.CustomerService;
import rx.Observable;

import javax.inject.Inject;

/**
 * Histrix command to update customer address
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class CustomerUpdateAddressCommand extends HystrixObservableCommand<Customer> {

    private final CustomerService customerService;
    private final CustomerId customerId;
    private final Address address;

    @Inject
    public CustomerUpdateAddressCommand(CustomerService customerService,
                                        @Assisted CustomerId customerId,
                                        @Assisted Address address) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CustomerUpdateAddress"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.customerService = customerService;
        this.customerId = customerId;
        this.address = address;
    }

    @Override
    protected Observable<Customer> construct() {
        return customerService.updateAddressObservable(customerId, address);
    }
}
