package com.senacor.reactile.gateway.commands;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.json.Jsonizable;
import com.senacor.reactile.service.branch.BranchList;
import com.senacor.reactile.service.branch.BranchService;

import io.vertx.core.json.JsonObject;
import rx.Observable;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class BranchesReadCommand extends HystrixObservableCommand<List<JsonObject>> {

    private final BranchService branchService;

    @Inject
    public BranchesReadCommand(BranchService branchService) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("FindBranches"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.branchService = branchService;
    }

    @Override
    protected Observable<List<JsonObject>> construct() {
        return branchService //
            .getAllBranches() //
            .map(BranchList::getBranches)
            .map(this::convertToJsonObject);
    }

    private List<JsonObject> convertToJsonObject(List<? extends Jsonizable> list) {
        return list.stream().map(Jsonizable::toJson).collect(Collectors.toList());
    }
}
