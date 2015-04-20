package com.senacor.reactile.customer;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;

/**
 * CustomerServiceImpl GetCustomer Command
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 17.04.15
 * Time: 15:30
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class CustomerServiceImplGetCustomerCommand extends InterceptableHystrixObservableCommand<Customer> {
    public CustomerServiceImplGetCustomerCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CustomerServiceImplGetCustomer"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
    }
}
