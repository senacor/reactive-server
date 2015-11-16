package com.senacor.reactile.service.branch;

import com.senacor.reactile.json.JsonizableList;
import rx.Observable;

import javax.inject.Inject;

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
