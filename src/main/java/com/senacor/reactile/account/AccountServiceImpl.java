package com.senacor.reactile.account;

import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.customer.CustomerServiceVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.math.BigDecimal;

import static com.senacor.reactile.account.Account.anAccount;
import static com.senacor.reactile.header.Headers.action;

public class AccountServiceImpl implements AccountService {
    private final Vertx vertx;

    public AccountServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

/*
    @Override
    public Observable<Account> getAccount(CustomerId customerId) {
        return Observable.just(anAccount()
                .withId("333")
                .withCustomerId(customerId)
                .withBalance(BigDecimal.TEN)
                .withCurrency("EUR")
                .build());
    }
*/

    @Override
    public Observable<Account> getAccount(AccountId accountId) {
        return vertx.eventBus().<Account>sendObservable(AccountServiceVerticle.ADDRESS, accountId, action("getAccount")).map(Message::body);
    }

    @Override
    public Observable<Account> getAccountsForCustomer(CustomerId customerId) {
        return vertx.eventBus().<Account>sendObservable(AccountServiceVerticle.ADDRESS, customerId, action("getAccountsForCustomer")).map(Message::body);
    }
}
