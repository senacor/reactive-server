package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.hystrix.interception.HystrixCmd;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

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
        getCreditCardsForCustomerHystrix(customerId)
                .subscribe(Rx.toSubscriber(resultHandler));
    }

    @HystrixCmd(CreditCardServiceImplGetCreditCardsForCustomerCommand.class)
    public Observable<List<JsonObject>> getCreditCardsForCustomerHystrix(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        return mongoService.findObservable(COLLECTION, query);
    }

    @Override
    public void createCreditCard(CreditCard creditCard, Handler<AsyncResult<String>> resultHandler) {
        JsonObject doc = creditCard.toJson().put("_id", creditCard.getId().toValue());
        mongoService.insert(COLLECTION, doc, resultHandler);

    }
}
