package com.senacor.reactile.account;

import com.senacor.reactile.IdObject;
import com.senacor.reactile.customer.CustomerId;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.util.List;

import static com.senacor.reactile.header.Headers.action;

public class TransactionServiceImpl implements TransactionService {
    private final Vertx vertx;

    public TransactionServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<List<Transaction>> getTransactionsForCustomer(CustomerId customerId) {
        return send(customerId, "getTransactionsForCustomer");
    }

    @Override
    public Observable<List<Transaction>> getTransactionsForAccount(AccountId accountId) {
        return send(accountId, "getTransactionsForAccount");
    }

    @Override
    public Observable<List<Transaction>> getTransactionsForCreditCard(CreditCardId creditCardId) {
        return send(creditCardId, "getTransactionsForCreditCard");
    }

    private Observable<List<Transaction>> send(IdObject id, String action) {
        return vertx.eventBus().<List<Transaction>>sendObservable(TransactionServiceVerticle.ADDRESS, id, action(action)).map(Message::body);
    }
}