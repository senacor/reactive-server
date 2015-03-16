package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.util.List;

import static com.senacor.reactile.header.Headers.action;

public class CreditCardServiceImpl implements CreditCardService {
    private final Vertx vertx;

    public CreditCardServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<CreditCard> getCreditCard(CreditCardId creditCardId) {
        return vertx.eventBus().<CreditCard>sendObservable(CreditCardServiceVerticle.ADDRESS, creditCardId, action("get")).map(Message::body);
    }

    @Override

    public Observable<List<CreditCard>> getCreditCardsForCustomer(CustomerId customerId) {
        return vertx.eventBus().<List<CreditCard>>sendObservable(CreditCardServiceVerticle.ADDRESS, customerId, action("getCreditCardsForCustomer")).map(Message::body);
    }

    @Override
    public Observable<CreditCard> createCreditCard(CreditCard creditCard) {
        return vertx.eventBus().<CreditCard>sendObservable(CreditCardServiceVerticle.ADDRESS, creditCard, action("create")).map(Message::body);
    }

}
