package com.senacor.reactile.service.branch;

import com.senacor.reactile.abstractservice.JsonizableList;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

public class BranchServiceImpl implements BranchService {

    @Inject
    BranchDatabase branchDatabase;

    public Observable<Branch> getBranch(String branchId) {
        return Observable.just(branchDatabase.findById(branchId));
    }

    public Observable<BranchList> findBranches(JsonizableList<String> branchIds) {
        return Observable.just(branchDatabase.findByIds(branchIds.toList()))
                .map(branches -> BranchList.newBuilder().withBranches(branches).build());
    }

    public Observable<BranchList> getAllBranches() {
        return Observable.just(branchDatabase.findAll())
                .map(branches -> BranchList.newBuilder().withBranches(branches).build());
    }

}
