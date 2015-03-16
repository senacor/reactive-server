package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.ObservableMongoService;
import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import rx.Observable;
import rx.functions.Func1;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class TransactionServiceVerticle extends AbstractServiceVerticle {
    public static final String ADDRESS = "TransactionServiceVerticle";

    private ObservableMongoService mongoService;
    private String collection;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        collection = context.config().getString("collection");
    }

    @Override
    public void start() throws Exception {
        super.start();
        MongoService eventBusProxy = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");
        mongoService = ObservableMongoService.from(eventBusProxy);
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
        return mongoService.insert(collection, transaction.toJson()).flatMap(id -> Observable.just(transaction));
    }

    private Observable<List<Transaction>> executeQuery(JsonObject query) {
        return mongoService.find(collection, query).map(toTransactionList());
    }

    private Func1<List<JsonObject>, List<Transaction>> toTransactionList() {
        return list -> list.stream().map(Transaction::fromJson).collect(toList());
    }
}