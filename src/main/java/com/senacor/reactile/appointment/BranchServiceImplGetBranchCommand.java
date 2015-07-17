package com.senacor.reactile.appointment;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;

/**
 * @author Andreas Keefer
 */
public class BranchServiceImplGetBranchCommand extends InterceptableHystrixObservableCommand<Branch> {
    public BranchServiceImplGetBranchCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("BranchServiceImplGetBranch"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
    }
}
