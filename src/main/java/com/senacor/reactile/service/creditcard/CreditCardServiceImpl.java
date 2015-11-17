package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.service.customer.CustomerId;
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
    public Observable<CreditCard> getCreditCard(CreditCardId creditCardId) {
        return mongoService
                .findOneObservable(COLLECTION, creditCardId.toJson(), null)
                .map(CreditCard::fromJson);
    }

    @Override
    public Observable<CreditCardList> getCreditCardsForCustomer(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        return mongoService.findObservable(COLLECTION, query)
                .flatMapIterable(x -> x)
                .map(CreditCard::fromJson)
                .collect(() -> new ArrayList<CreditCard>(), (list, cc) -> list.add(cc))
                .map(CreditCardList::new);
    }

    @Override
    public Observable<CreditCard> createCreditCard(CreditCard creditCard) {
        JsonObject doc = creditCard.toJson().put("_id", creditCard.getId().toValue());
        return mongoService.insertObservable(COLLECTION, doc).map(id -> creditCard);
    }
}
