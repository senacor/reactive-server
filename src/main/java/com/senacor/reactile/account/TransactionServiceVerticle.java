package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rwinzing on 03.03.15.
 */
public class TransactionServiceVerticle extends AbstractServiceVerticle implements TransactionService{
    public static final String ADDRESS = "TransactionServiceVerticle";

    private MongoService mongoService;

    @Override
    public void start() throws Exception {
        super.start();
        mongoService = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");
    }


    @Action
    public Observable<List<Transaction>> getTransactionsForCustomer(CustomerId customerId) {
        ObservableFuture<List<JsonObject>> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("customerId", customerId.toValue());
        mongoService.find("transactions", query, observable.toHandler());

        return observable.map(list -> {
            List<Transaction> txs = new ArrayList<Transaction>();
            list.forEach(item -> txs.add(Transaction.fromJson(item)));
            return txs;
        });
    }

    @Action
    public Observable<List<Transaction>> getTransactionsForAccount(AccountId accountId) {
        ObservableFuture<List<JsonObject>> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("accountId", accountId.getId());
        mongoService.find("transactions", query, observable.toHandler());

        return observable.map(list -> {
            List<Transaction> txs = new ArrayList<Transaction>();
            list.forEach(item -> txs.add(Transaction.fromJson(item)));
            return txs;
        });
    }

    @Action
    public Observable<List<Transaction>> getTransactionsForCreditCard(CreditCardId creditCardId) {
        ObservableFuture<List<JsonObject>> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("creditCardId", creditCardId.getId());
        mongoService.find("transactions", query, observable.toHandler());

        return observable.map(list -> {
            List<Transaction> txs = new ArrayList<Transaction>();
            list.forEach(item -> txs.add(Transaction.fromJson(item)));
            return txs;
        });
    }
}