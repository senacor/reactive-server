package com.senacor.reactile.service.branch;

import com.senacor.reactile.json.JsonizableList;
import rx.Observable;

import java.util.List;

public class BranchServiceImpl implements BranchService {
    private final BranchDatabase branchDatabase = new BranchDatabase();

    @Override
    public Observable<Branch> getBranch(String branchId) {
        return Observable.defer(()-> Observable.just(branchDatabase.findById(branchId)));
    }

    @Override
    public Observable<BranchList> findBranches(JsonizableList<String> branchIds) {
        return Observable.defer(()-> {
            List<Branch> branches = branchDatabase.findByIds(branchIds.toList());
            BranchList branchList = BranchList.newBuilder().withBranches(branches).build();
            return Observable.just(branchList);
        });
    }

    @Override
    public Observable<BranchList> getAllBranches() {
        return Observable.defer(()-> {
            List<Branch> branches = branchDatabase.findAll();
            BranchList branchList = BranchList.newBuilder().withBranches(branches).build();
            return Observable.just(branchList);
        });
    }
}
