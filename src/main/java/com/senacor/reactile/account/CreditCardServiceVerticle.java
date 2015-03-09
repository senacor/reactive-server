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
public class CreditCardServiceVerticle extends AbstractServiceVerticle implements CreditCardService {
    public static final String ADDRESS = "CreditCardServiceVerticle";

    private MongoService mongoService;

    @Override
    public void start() throws Exception {
        super.start();
        mongoService = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");
    }

    @Action
    public Observable<List<CreditCard>> getCreditCardsForCustomer(CustomerId customerId) {
        ObservableFuture<List<JsonObject>> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        mongoService.find("creditcards", query, observable.toHandler());

        return observable.map(list -> {
            List<CreditCard> ccs = new ArrayList<CreditCard>();
            list.forEach(item -> ccs.add(CreditCard.fromJson(item)));
            return ccs;
        });
        // return observable.flatMap(Observable::from).map(Account::fromJson);
    }

    @Action
    public Observable<CreditCard> getCreditCard(CreditCardId creditCardId) {
        ObservableFuture<JsonObject> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("id", creditCardId.getId());
        mongoService.findOne("creditcards", query, null, observable.toHandler());

        return observable.map(CreditCard::fromJson);
    }
}
