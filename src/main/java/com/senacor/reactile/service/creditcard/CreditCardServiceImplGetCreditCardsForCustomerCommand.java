package com.senacor.reactile.service.creditcard;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;

/**
 * CreditCardServiceImpl#getCreditCardsForCustomer HystrixCommand
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 20.04.15
 * Time: 13:42
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class CreditCardServiceImplGetCreditCardsForCustomerCommand extends InterceptableHystrixObservableCommand {

    public CreditCardServiceImplGetCreditCardsForCustomerCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetCreditCardsForCustomer"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
    }
}
