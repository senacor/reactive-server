package com.senacor.reactile.service.branch;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.json.JsonizableList;
import rx.Observable;

public interface BranchService {

    @Action(returnType = Branch.class)
    public Observable<Branch> getBranch(BranchId branchId);

    @Action(returnType = BranchList.class)
    public Observable<BranchList> findBranches(JsonizableList<BranchId> branchIds);

    @Action(returnType = BranchList.class)
    public Observable<BranchList> getAllBranches();


}
