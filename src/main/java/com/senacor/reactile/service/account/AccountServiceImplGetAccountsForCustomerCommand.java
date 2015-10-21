package com.senacor.reactile.service.account;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * AccountServiceImpl#getAccountsForCustomer HystrixCommand
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 20.04.15
 * Time: 11:36
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class AccountServiceImplGetAccountsForCustomerCommand extends InterceptableHystrixObservableCommand<List<JsonObject>> {

    private final CustomerId customerId;

    public AccountServiceImplGetAccountsForCustomerCommand(CustomerId customerId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetAccountsForCustomer"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        notNull(customerId, "customerId must not be null");
        this.customerId = customerId;
    }

    // TODO (ak) To use request-scoped features (request caching, request collapsing, request log) you must manage the HystrixRequestContext lifecycle (or implement an alternative HystrixConcurrencyStrategy).
//    @Override
//    protected String getCacheKey() {
//        return customerId.getId();
//    }
}
