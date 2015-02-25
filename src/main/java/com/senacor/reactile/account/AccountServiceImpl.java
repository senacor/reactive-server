package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import java.math.BigDecimal;

import static com.senacor.reactile.account.Account.anAccount;

public class AccountServiceImpl implements AccountService {
    private final Vertx vertx;

    public AccountServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<Account> getAccount(CustomerId customerId) {
        return Observable.just(anAccount()
                .withId("333")
                .withCustomerId(customerId.getId())
                .withBalance(BigDecimal.TEN)
                .withCurrency("EUR")
                .build());
    }

}
