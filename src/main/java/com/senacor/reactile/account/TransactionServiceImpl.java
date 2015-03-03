package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.math.BigDecimal;
import java.util.List;

import static com.senacor.reactile.account.Transaction.aTransaction;
import static com.senacor.reactile.header.Headers.action;

public class TransactionServiceImpl implements TransactionService {
    private final Vertx vertx;

    public TransactionServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<List<Transaction>> getTransactionsForCustomer(CustomerId customerId) {
        return vertx.eventBus().<List<Transaction>>sendObservable(TransactionServiceVerticle.ADDRESS, customerId, action("getTransactionsForCustomer")).map(Message::body);
    }

    @Override
    public Observable<List<Transaction>> getTransactionsForAccount(AccountId accountId) {
        return vertx.eventBus().<List<Transaction>>sendObservable(TransactionServiceVerticle.ADDRESS, accountId, action("getTransactionsForAccount")).map(Message::body);
    }
    @Override
    public Observable<List<Transaction>> getTransactionsForCreditCard(CreditCardId creditCardId) {
        return vertx.eventBus().<List<Transaction>>sendObservable(TransactionServiceVerticle.ADDRESS, creditCardId, action("getTransactionsForCreditCard")).map(Message::body);
    }
}