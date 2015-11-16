package com.senacor.reactile.gateway.commands;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.abstractservice.JsonizableList;
import com.senacor.reactile.service.user.UserService;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by swalter on 23.10.15.
 */
public class UserFindCommand extends HystrixObservableCommand<List<JsonObject>> {

    private final UserService userService;
    private Map<String, String> params;


    @Inject
    public UserFindCommand(UserService userService,
                           @Assisted Map<String,String> params) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("FindUser"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.userService = userService;
        this.params = params;
    }

    @Override
    protected Observable<List<JsonObject>> construct() {
        JsonObject query = new JsonObject();
        for(Map.Entry<String, String> e : params.entrySet()) {
            query.put(e.getKey(), e.getValue());
        }

        return userService.findUser(query)
                .map(JsonizableList::toList);
    }

}
