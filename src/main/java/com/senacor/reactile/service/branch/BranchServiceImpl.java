package com.senacor.reactile.service.branch;

import com.senacor.reactile.json.JsonizableList;
import rx.Observable;

public class BranchServiceImpl implements BranchService {
    @Override
    public Observable<Branch> getBranch(String branchId) {
        return null;
    }

    @Override
    public Observable<BranchList> findBranches(JsonizableList<String> branchIds) {
        return null;
    }

    @Override
    public Observable<BranchList> getAllBranches() {
        return null;
    }
}
