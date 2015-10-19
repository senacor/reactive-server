package com.senacor.reactile.service.account;

import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TransactionServiceVerticle extends AbstractServiceVerticle {
    public static final String ADDRESS = "TransactionServiceVerticle";

    private final MongoService mongoService;
    private String collection;

    @Inject
    public TransactionServiceVerticle(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        collection = context.config().getString("collection");
    }

    @Action
    public Observable<List<Transaction>> getTransactionsForCustomer(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.toValue());
        return executeQuery(query);
    }

    @Action
    public Observable<List<Transaction>> getTransactionsForAccount(AccountId accountId) {
        JsonObject query = new JsonObject().put("accountId", accountId.getId());
        return executeQuery(query);
    }

    @Action
    public Observable<List<Transaction>> getTransactionsForCreditCard(CreditCardId creditCardId) {
        JsonObject query = new JsonObject().put("creditCardId", creditCardId.getId());
        return executeQuery(query);
    }


    @Action("create")
    public Observable<Transaction> addTransaction(Transaction transaction) {
        return mongoService.insertObservable(collection, transaction.toJson()).flatMap(id -> Observable.just(transaction));
    }

    private Observable<List<Transaction>> executeQuery(JsonObject query) {
        return mongoService.findObservable(collection, query).map(toTransactionList());
    }

    private Func1<List<JsonObject>, List<Transaction>> toTransactionList() {
        return list -> list.stream().map(Transaction::fromJson).collect(toList());
    }
}