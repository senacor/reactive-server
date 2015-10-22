package com.senacor.reactile.service.branch;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

public class BranchServiceImpl implements BranchService {

    @Inject
    BranchDatabase branchDatabase;

    public void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler) {
        Rx.bridgeHandler(getBranch(branchId), resultHandler);
    }

    private Observable<Branch> getBranch(String branchId) {
        return Observable.just(branchDatabase.findById(branchId));
    }


    public void findBranches(List<String> branchIds, Handler<AsyncResult<BranchList>> resultHandler) {
        Rx.bridgeHandler(getBranches(branchIds), resultHandler);
    }

    private Observable<BranchList> getBranches(List<String> branchIds) {
        return Observable.just(branchDatabase.findByIds(branchIds))
                .map(branches -> BranchList.newBuilder().withBranches(branches).build());
    }

    public void getAllBranches(Handler<AsyncResult<BranchList>> resultHandler) {
        Rx.bridgeHandler(getBranches(), resultHandler);
    }

    private Observable<BranchList> getBranches() {
        return Observable.just(branchDatabase.findAll())
                .map(branches -> BranchList.newBuilder().withBranches(branches).build());
    }

}
