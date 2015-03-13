package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.util.List;

import static com.senacor.reactile.header.Headers.action;

public class AccountServiceImpl implements AccountService {
    private final Vertx vertx;

    public AccountServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<Account> getAccount(AccountId accountId) {
        return vertx.eventBus().<Account>sendObservable(AccountServiceVerticle.ADDRESS, accountId, action("get")).map(Message::body);
    }

    @Override
    public Observable<List<Account>> getAccountsForCustomer(CustomerId customerId) {
        return vertx.eventBus().<List<Account>>sendObservable(AccountServiceVerticle.ADDRESS, customerId, action("getAccountsForCustomer")).map(Message::body);
    }
}
