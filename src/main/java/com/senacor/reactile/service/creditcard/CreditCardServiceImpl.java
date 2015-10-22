package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.rx.Rx;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;
import java.util.ArrayList;

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
    public void getCreditCardsForCustomer(CustomerId customerId, Handler<AsyncResult<CreditCardList>> resultHandler) {
        Rx.bridgeHandler(getCreditCardsForCustomer(customerId), resultHandler);
    }

    public Observable<CreditCardList> getCreditCardsForCustomer(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        return mongoService.findObservable(COLLECTION, query)
                .flatMapIterable(x -> x)
                .map(CreditCard::fromJson)
                .collect(() -> new ArrayList<CreditCard>(), (list, cc) -> list.add(cc))
                .map(CreditCardList::new);
    }

    @Override
    public void createCreditCard(CreditCard creditCard, Handler<AsyncResult<CreditCard>> resultHandler) {
        JsonObject doc = creditCard.toJson().put("_id", creditCard.getId().toValue());
        Rx.bridgeHandler(mongoService.insertObservable(COLLECTION, doc).map(id -> creditCard), resultHandler);

    }
}
