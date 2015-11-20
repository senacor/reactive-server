package com.senacor.reactile.gateway.commands;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.json.JsonizableList;
import com.senacor.reactile.service.branch.Branch;
import com.senacor.reactile.service.branch.BranchService;
import com.senacor.reactile.service.user.UserService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import javax.inject.Inject;

public class GetBranchWithUsersCommand extends HystrixObservableCommand<JsonObject> {

    private final BranchService branchService;
    private final UserService userService;
    private final String branchId;

    @Inject
    public GetBranchWithUsersCommand(BranchService branchService, UserService userService, @Assisted String branchId) {

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Gateway"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetBranchWithUsers"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50))
        );

        this.branchService = branchService;
        this.userService = userService;
        this.branchId = branchId;
    }

    @Override
    protected Observable<JsonObject> construct() {
        Observable<JsonObject> branch = branchService.getBranch(branchId).map(Branch::toJson);
        Observable<JsonizableList<JsonObject>> usersOfBranch = userService.findUser(new JsonObject().put("branchId", branchId));

        return Observable.zip(branch, usersOfBranch, this::combineBranchWithUsers);
    }

    private JsonObject combineBranchWithUsers(JsonObject branch, JsonizableList<JsonObject> usersOfBranch) {
        return new JsonObject()
                .put("branch", branch)
                .put("users", new JsonArray(usersOfBranch.toList()));
    }
}
