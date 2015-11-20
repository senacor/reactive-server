package com.senacor.reactile.gateway.commands;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.service.branch.BranchList;
import com.senacor.reactile.service.branch.BranchService;
import rx.Observable;

import javax.inject.Inject;

public class BranchReadCommand extends HystrixObservableCommand<BranchList>{

    private final BranchService branchService;

    @Inject
    protected BranchReadCommand(BranchService branchService) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Wurstaffen"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetAllBranches"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.branchService = branchService;
    }

    @Override
    protected Observable<BranchList> construct() {
        return branchService.getAllBranches();
    }
}
