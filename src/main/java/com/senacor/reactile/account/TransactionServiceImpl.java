package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import java.math.BigDecimal;

import static com.senacor.reactile.account.Transaction.aTransaction;

public class TransactionServiceImpl implements TransactionService {
    private final Vertx vertx;

    public TransactionServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<Transaction> getTransaction(CustomerId customerId) {
        return Observable.just(
                aTransaction().withCustomerId(customerId.getId()).withAccountId(customerId.getId()+"-ac-01").withAmount(new BigDecimal("18773")).withCurrency("EUR").build(),
                aTransaction().withCustomerId(customerId.getId()).withAccountId(customerId.getId()+"-cc-01").withAmount(new BigDecimal("20770")).withCurrency("EUR").build()
        );
    }

    @Override
    public Observable<Transaction> getTransaction(CustomerId customerId, AccountId accountId) {
        return Observable.just(
                aTransaction().withCustomerId(customerId.getId()).withAccountId(customerId.getId()+"-ac-01").withAmount(new BigDecimal("17805")).withCurrency("EUR").build()
        );
    }
    @Override
    public Observable<Transaction> getTransaction(CustomerId customerId, CreditCardId creditCardId) {
        return Observable.just(
                aTransaction().withCustomerId(customerId.getId()).withAccountId(customerId.getId()+"-cc-01").withAmount(new BigDecimal("14597")).withCurrency("EUR").build()
        );
    }
}