package com.senacor.reactile.creditcard;

import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import java.util.List;

public class CreditCardServiceImpl implements CreditCardService {
    public static final String COLLECTION = "creditcards";
    private final ObservableMongoService mongoService;

    @Inject
    public CreditCardServiceImpl(ObservableMongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getCreditCard(CreditCardId creditCardId, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoService.findOne(COLLECTION, creditCardId.toJson(), null, resultHandler);
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
