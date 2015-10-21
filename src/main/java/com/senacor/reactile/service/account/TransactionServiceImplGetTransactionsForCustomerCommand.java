package com.senacor.reactile.service.account;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;

import java.util.List;

/**
 * TransactionServiceImpl#getTransactionsForCustomer HystrixCommand
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 20.04.15
 * Time: 14:06
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class TransactionServiceImplGetTransactionsForCustomerCommand extends InterceptableHystrixObservableCommand<List<Transaction>> {
    public TransactionServiceImplGetTransactionsForCustomerCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetTransactionsForCustomer"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
    }
}
