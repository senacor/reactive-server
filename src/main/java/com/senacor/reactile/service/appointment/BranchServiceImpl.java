package com.senacor.reactile.service.appointment;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import javax.inject.Inject;
import java.util.List;

public class BranchServiceImpl implements BranchService {

    private final BranchDatabase branchDatabase;

    @Inject
    public BranchServiceImpl(BranchDatabase branchDatabase) {
        this.branchDatabase = branchDatabase;
    }

    @Override
    public void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler) {
    }

    @Override
    public void findBranches(List<String> branchIds, Handler<AsyncResult<BranchList>> resultHandler) {
    }

    @Override
    public void getAllBranches(Handler<AsyncResult<BranchList>> resultHandler) {
    }

}
