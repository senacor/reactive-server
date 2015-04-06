package com.senacor.reactile.creditcard;

import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;

import javax.inject.Inject;
import java.util.List;

public class CreditCardServiceImpl implements CreditCardService {
    public static final String COLLECTION = "creditcards";
    private final MongoService mongoService;

    @Inject
    public CreditCardServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getCreditCard(CreditCardId creditCardId, Handler<AsyncResult<CreditCard>> resultHandler) {
        Rx.bridgeHandler(mongoService.findOneObservable(COLLECTION, creditCardId.toJson(), null).map(CreditCard::fromJson), resultHandler);
    }

    @Override
    public void getCreditCardsForCustomer(CustomerId customerId, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        mongoService.find(COLLECTION, query, resultHandler);
    }

    @Override
    public void createCreditCard(CreditCard creditCard, Handler<AsyncResult<String>> resultHandler) {
        JsonObject doc = creditCard.toJson().put("_id", creditCard.getId().toValue());
        mongoService.insert(COLLECTION, doc, resultHandler);

    }
}
