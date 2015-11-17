package com.senacor.reactile.service.branch;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.json.JsonizableList;
import rx.Observable;

public interface BranchService {

    @Action(returnType = Branch.class)
    public Observable<Branch> getBranch(String branchId);

    @Action(returnType = BranchList.class)
    public Observable<BranchList> findBranches(JsonizableList<String> branchIds);

    @Action(returnType = BranchList.class)
    public Observable<BranchList> getAllBranches();


}
