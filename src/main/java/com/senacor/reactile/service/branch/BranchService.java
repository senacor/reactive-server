package com.senacor.reactile.service.branch;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public interface BranchService {

    String ADDRESS = "BranchService";

    void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler);

    void findBranches(List<String> branchIds, Handler<AsyncResult<BranchList>> resultHandler);

    void getAllBranches(Handler<AsyncResult<BranchList>> resultHandler);


}
