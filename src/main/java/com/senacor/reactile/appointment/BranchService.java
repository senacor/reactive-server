package com.senacor.reactile.appointment;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface BranchService {

    static final String ADDRESS = "BranchService";

    void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler);

}
