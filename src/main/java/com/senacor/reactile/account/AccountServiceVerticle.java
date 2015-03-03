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
public class AccountServiceVerticle extends AbstractServiceVerticle {
    public static final String ADDRESS = "AccountServiceVerticle";

    private MongoService mongoService;

    @Override
    public void start() throws Exception {
        super.start();
        mongoService = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");
    }

    @Action
    public Observable<List<Account>> getAccountsForCustomer(CustomerId id) {
        ObservableFuture<List<JsonObject>> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("customerId", id.toValue());
        mongoService.find("accounts", query, observable.asHandler());

        return observable.map(list -> {
            List<Account> accs = new ArrayList<Account>();
            list.forEach(item -> accs.add(Account.fromJson(item)));
            return accs;
        });
        // return observable.flatMap(Observable::from).map(Account::fromJson);
    }

    @Action
    public Observable<Account> getAccount(AccountId id) {
        ObservableFuture<JsonObject> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("id", id.toValue());
        mongoService.findOne("accounts", query, null, observable.asHandler());

        return observable.map(Account::fromJson);
    }
}
