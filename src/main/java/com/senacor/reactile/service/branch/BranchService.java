package com.senacor.reactile.service.branch;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.abstractservice.JsonizableList;
import com.senacor.reactile.domain.Jsonizable;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

import java.util.List;

public interface BranchService {

    @Action(returnType = Branch.class)
    public Observable<Branch> getBranch(String branchId);

    @Action(returnType = BranchList.class)
    public Observable<BranchList> findBranches(JsonizableList<String> branchIds);

    @Action(returnType = BranchList.class)
    public Observable<BranchList> getAllBranches();


}
