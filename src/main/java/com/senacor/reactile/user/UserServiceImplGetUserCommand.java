package com.senacor.reactile.user;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * UserServiceImpl#getUser Hystrix Command
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 20.04.15
 * Time: 11:13
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class UserServiceImplGetUserCommand extends InterceptableHystrixObservableCommand<User> {

    private final UserId userId;

    public UserServiceImplGetUserCommand(UserId userId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserServiceImplGetUser"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        notNull(userId, "userId must not be null");
        this.userId = userId;
    }

    // TODO (ak) To use request-scoped features (request caching, request collapsing, request log) you must manage the HystrixRequestContext lifecycle (or implement an alternative HystrixConcurrencyStrategy).
//    @Override
//    protected String getCacheKey() {
//        return userId.getId();
//    }
}
