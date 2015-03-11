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
public class CreditCardServiceVerticle extends AbstractServiceVerticle implements CreditCardService {
    public static final String ADDRESS = "CreditCardServiceVerticle";

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
    public Observable<List<CreditCard>> getCreditCardsForCustomer(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        return mongoService.find(collection, query)
                .map(list -> list.stream().map(CreditCard::fromJson).collect(toList()));
    }

    @Action("get")
    public Observable<CreditCard> getCreditCard(CreditCardId creditCardId) {
        JsonObject query = new JsonObject().put("id", creditCardId.getId());
        return mongoService.findOne("creditcards", query).map(CreditCard::fromJson);
    }
}
