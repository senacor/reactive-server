package com.senacor.reactile.service.account;

import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

@ProxyGen
@VertxGen
public interface AccountService {
    static final String ADDRESS = "AccountService";

    void getAccount(AccountId accountId, Handler<AsyncResult<Account>> resultHandler);

    void getAccountsForCustomer(CustomerId customerId, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    void createAccount(Account account, Handler<AsyncResult<Account>> resultHandler);
}
