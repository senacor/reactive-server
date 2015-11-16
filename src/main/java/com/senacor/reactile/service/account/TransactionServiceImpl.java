package com.senacor.reactile.service.account;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.rx.Rx;
import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TransactionServiceImpl implements TransactionService {
    public static final String COLLECTION = "transactions";
    private final MongoService mongoService;

    @Inject
    public TransactionServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public Observable<TransactionList> getTransactionsForCustomer(CustomerId customerId){
        JsonObject query = new JsonObject().put("customerId", customerId.toValue());
        return executeQuery(query);
    }

    @Override
    public  Observable<TransactionList> getTransactionsForAccount(AccountId accountId) {
        JsonObject query = new JsonObject().put("accountId", accountId.getId());
        return executeQuery(query);
    }

    @Override
    public Observable<TransactionList> getTransactionsForCreditCard(CreditCardId creditCardId) {
        JsonObject query = new JsonObject().put("creditCardId", creditCardId.getId());
        return executeQuery(query);
    }

    @Override
    public Observable<Transaction> createTransaction(Transaction transaction){
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