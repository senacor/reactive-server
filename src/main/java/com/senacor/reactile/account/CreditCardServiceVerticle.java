package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.ObservableMongoService;
import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CreditCardServiceVerticle extends AbstractServiceVerticle {
    public static final String ADDRESS = "CreditCardServiceVerticle";

    private final ObservableMongoService mongoService;
    private String collection;

    @Inject
    public CreditCardServiceVerticle(ObservableMongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        collection = context.config().getString("collection");
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
        return mongoService.findOne(collection, query).map(CreditCard::fromJson);
    }


    @Action("create")
    public Observable<CreditCard> addCreditCard(CreditCard creditCard) {
        return mongoService.insert(collection, creditCard.toJson()).flatMap(id -> Observable.just(creditCard));
    }
}
