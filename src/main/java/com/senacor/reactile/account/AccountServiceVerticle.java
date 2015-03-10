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

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by rwinzing on 03.03.15.
 */
public class AccountServiceVerticle extends AbstractServiceVerticle implements AccountService {
    public static final String ADDRESS = "AccountServiceVerticle";

    private ObservableMongoService mongoService;
    private String collection;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        collection = context.config().getString("collection");
    }

    @Override
    public void start() throws Exception {
        MongoService eventBusProxy = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");
        super.start();
        //TODO configuration and guice injection
        mongoService = ObservableMongoService.from(eventBusProxy);
    }

    @Action
    public Observable<List<Account>> getAccountsForCustomer(CustomerId id) {
        JsonObject query = new JsonObject().put("customerId", id.toValue());
        return mongoService.find(collection, query)
                .map(list -> list.stream().map(Account::fromJson).collect(toList()));
    }

    @Action("get")
    public Observable<Account> getAccount(AccountId id) {
        JsonObject query = new JsonObject().put("id", id.toValue());
        return mongoService.findOne("accounts", query).map(Account::fromJson);
    }
}
