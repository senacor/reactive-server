package com.senacor.reactile.service.branch;

import com.google.inject.Inject;
import com.senacor.reactile.json.JsonizableList;
import rx.Observable;

import java.util.List;

/**
 * Created by hannes on 19/11/15.
 */
public class BranchServiceImpl implements BranchService {

    private final BranchDatabase branchDatabase;

    @Inject
    public BranchServiceImpl(BranchDatabase branchDatabase) {
        this.branchDatabase = branchDatabase;
    }

    @Override
    public Observable<Branch> getBranch(String branchId) {
        return Observable.just(branchDatabase.findById(branchId));
    }

    @Override
    public Observable<BranchList> findBranches(JsonizableList<String> branchIds) {
        final List<Branch> branches = branchDatabase.findByIds(branchIds.toList());
        return Observable.just(new BranchList(branches));
    }

    @Override
    public Observable<BranchList> getAllBranches() {
        final List<Branch> branches = branchDatabase.findAll();
        return Observable.just(new BranchList(branches));
    }
}
