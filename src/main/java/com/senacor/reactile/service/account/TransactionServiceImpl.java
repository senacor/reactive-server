package com.senacor.reactile.service.account;

import com.senacor.reactile.rx.Rx;
import com.senacor.reactile.service.Action;
import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.hystrix.interception.HystrixCmd;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.senacor.reactile.header.Headers.action;
import static java.util.stream.Collectors.toList;

public class TransactionServiceImpl implements TransactionService {
    public static final String COLLECTION = "transactions";
    private final MongoService mongoService;

    @Inject
    public TransactionServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getTransactionsForCustomer(CustomerId customerId, Handler<AsyncResult<TransactionList>> resultHandler) {
        Rx.bridgeHandler(getTransactionsForCustomer(customerId), resultHandler);
    }

    @Override
    public void getTransactionsForAccount(AccountId accountId, Handler<AsyncResult<TransactionList>> resultHandler) {
        Rx.bridgeHandler(getTransactionsForAccount(accountId), resultHandler);
    }

    @Override
    public void getTransactionsForCreditCard(CreditCardId creditCardId, Handler<AsyncResult<TransactionList>> resultHandler) {
        Rx.bridgeHandler(getTransactionsForCreditCard(creditCardId), resultHandler);
    }

    @Override
    public void createTransaction(Transaction transaction, Handler<AsyncResult<Transaction>> resultHandler) {
        Rx.bridgeHandler(addTransaction(transaction), resultHandler);
    }

    @HystrixCmd(TransactionServiceImplGetTransactionsForCustomerCommand.class)
    private Observable<TransactionList> getTransactionsForCustomer(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.toValue());
        return executeQuery(query);
    }

    private Observable<TransactionList> getTransactionsForAccount(AccountId accountId) {
        JsonObject query = new JsonObject().put("accountId", accountId.getId());
        return executeQuery(query);
    }

    private Observable<TransactionList> getTransactionsForCreditCard(CreditCardId creditCardId) {
        JsonObject query = new JsonObject().put("creditCardId", creditCardId.getId());
        return executeQuery(query);
    }


    private Observable<Transaction> addTransaction(Transaction transaction) {
        JsonObject doc = transaction.toJson().put("_id", transaction.getId().toValue());
        Observable<String> stringObservable = mongoService.insertObservable(COLLECTION, doc);
        return stringObservable.flatMap(id -> Observable.just(transaction));
    }

    private Observable<TransactionList> executeQuery(JsonObject query) {
        return mongoService.findObservable(COLLECTION, query).map(toTransactionList());
    }

    private Func1<List<JsonObject>, TransactionList> toTransactionList() {
        return list -> {
            List<Transaction> transactions = list.stream().map(Transaction::fromJson).collect(toList());
            return new TransactionList(transactions);
        };
    }
}