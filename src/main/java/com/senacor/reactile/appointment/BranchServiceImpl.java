package com.senacor.reactile.appointment;

import com.google.inject.Inject;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

public class BranchServiceImpl implements BranchService {

    private final BranchDatabase branchDatabase;

    @Inject
    public BranchServiceImpl(BranchDatabase branchDatabase) {
        this.branchDatabase = branchDatabase;
    }

    @Override
    public void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler) {
        Rx.bridgeHandler(getBranch(branchId), resultHandler);
    }

    public Observable<Branch> getBranch(String branchId) {
        // TODO
        return null;
    }
}
