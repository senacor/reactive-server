package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.math.BigDecimal;
import java.util.List;

import static com.senacor.reactile.account.CreditCard.aCreditCard;
import static com.senacor.reactile.header.Headers.action;

public class CreditCardServiceImpl implements CreditCardService {
    private final Vertx vertx;

    public CreditCardServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    /*
    @Override
    public Observable<CreditCard> getCreditCard(CustomerId customerId) {
        return Observable.just(aCreditCard()
                .withId("333")
                .withCustomerId(customerId)
                .withBalance(BigDecimal.TEN)
                .withCurrency("EUR")
                .build());
    }
    */

    @Override
    public Observable<CreditCard> getCreditCard(CreditCardId creditCardId) {
        return vertx.eventBus().<CreditCard>sendObservable(CreditCardServiceVerticle.ADDRESS, creditCardId, action("getCreditCard")).map(Message::body);
    }

    @Override

    public Observable<List<CreditCard>> getCreditCardsForCustomer(CustomerId customerId) {
        return vertx.eventBus().<List<CreditCard>>sendObservable(CreditCardServiceVerticle.ADDRESS, customerId, action("getCreditCardsForCustomer")).map(Message::body);
    }

}
