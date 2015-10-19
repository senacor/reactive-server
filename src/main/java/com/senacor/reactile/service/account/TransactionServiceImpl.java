package com.senacor.reactile.service.account;

import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.hystrix.interception.HystrixCmd;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

import static com.senacor.reactile.header.Headers.action;

public class TransactionServiceImpl implements TransactionService {
    private final Vertx vertx;

    @Inject
    public TransactionServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @HystrixCmd(TransactionServiceImplGetTransactionsForCustomerCommand.class)
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

    @Override
    public Observable<Transaction> createTransaction(Transaction transaction) {
        return send(transaction, "create");
    }

    private <T> Observable<T> send(Object message, String action) {
        return vertx.eventBus().<T>sendObservable(TransactionServiceVerticle.ADDRESS, message, action(action)).map(Message::body);
    }
}