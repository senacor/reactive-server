package com.senacor.reactile.service.branch;

import com.senacor.reactile.service.customer.Address;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

@ProxyGen
@VertxGen
public interface BranchService {

    String ADDRESS = "BranchService";

    String ADDRESS_EVENT_UPDATE_ADDRESS = ADDRESS + "#updateAddress";

    void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler);

    void findBranches(List<String> branchIds, Handler<AsyncResult<BranchList>> resultHandler);

    void getAllBranches(Handler<AsyncResult<BranchList>> resultHandler);

    void updateAddress(String branchId, Address address, Handler<AsyncResult<Branch>> resultHandler);

}
