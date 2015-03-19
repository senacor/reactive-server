package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

import java.util.List;

import static java.util.stream.Collectors.toList;

@ProxyGen

public interface AccountService {
    static final String ADDRESS = "AccountService";

    @GenIgnore
    default Observable<Account> getAccount(AccountId accountId) {
        ObservableFuture<Account> observableFuture = RxHelper.observableFuture();
        getAccount(accountId, observableFuture.toHandler());
        return observableFuture;
    }

    @GenIgnore
    default Observable<List<Account>> getAccountsForCustomer(CustomerId customerId){
        ObservableFuture<List<JsonObject>> observableFuture = RxHelper.observableFuture();
        getAccountsForCustomer(customerId, observableFuture.toHandler());
        return observableFuture.map(list -> list.stream().map(Account::fromJson).collect(toList()));
    }

    @GenIgnore
    default Observable<Account> createAccount(Account account) {
        ObservableFuture<String> observableFuture = RxHelper.observableFuture();
        createAccount(account, observableFuture.toHandler());
        return observableFuture.flatMap(id -> Observable.just(account));
    }

    void getAccount(AccountId accountId, Handler<AsyncResult<Account>> resultHandler);

    void getAccountsForCustomer(CustomerId customerId, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    void createAccount(Account account, Handler<AsyncResult<String>> resultHandler);
}
