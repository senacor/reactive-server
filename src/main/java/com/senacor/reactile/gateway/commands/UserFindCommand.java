package com.senacor.reactile.gateway.commands;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.rxjava.service.user.UserService;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by swalter on 23.10.15.
 */
public class UserFindCommand extends HystrixObservableCommand<List<JsonObject>> {

    private final UserService userService;


    @Inject
    public UserFindCommand(UserService userService) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("FindUser"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.userService = userService;
    }

    @Override
    protected Observable<List<JsonObject>> construct() {
        JsonObject query = new JsonObject();
                /*Observable.from(paramMap.names())
                .collect(JsonObject::new, (r, s) -> r.put(s, paramMap.get(s)))
                .toBlocking().first();*/

        return userService.findUserObservable(query);
    }

}
