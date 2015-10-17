package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

import java.util.List;

import static java.util.stream.Collectors.toList;

@ProxyGen
@VertxGen
public interface AccountService {
    static final String ADDRESS = "AccountService";

    void getAccount(AccountId accountId, Handler<AsyncResult<Account>> resultHandler);

    void getAccountsForCustomer(CustomerId customerId, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    void createAccount(Account account, Handler<AsyncResult<Account>> resultHandler);
}
