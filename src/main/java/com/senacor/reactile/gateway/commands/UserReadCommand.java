package com.senacor.reactile.gateway.commands;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.service.user.User;
import com.senacor.reactile.service.user.UserId;
import com.senacor.reactile.service.user.UserService;
import rx.Observable;

import javax.inject.Inject;

/**
 * Created by sfuss on 22.10.15.
 */
public class UserReadCommand extends HystrixObservableCommand<User> {

    private final UserService userService;
    private final UserId userId;


    @Inject
    public UserReadCommand(UserService userService,
                                        @Assisted UserId userId) {
        super(HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetUser"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.userService = userService;
        this.userId = userId;
    }

    @Override
    protected Observable<User> construct() {
        return userService.getUser(userId);
    }

}
