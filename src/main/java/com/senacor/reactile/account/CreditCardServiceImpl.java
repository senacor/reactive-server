package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import java.math.BigDecimal;

import static com.senacor.reactile.account.CreditCard.aCreditCard;

public class CreditCardServiceImpl implements CreditCardService {
    private final Vertx vertx;

    public CreditCardServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<CreditCard> getCreditCard(CustomerId customerId) {
        return Observable.just(aCreditCard()
                .withId("333")
                .withCustomerId(customerId.getId())
                .withBalance(BigDecimal.TEN)
                .withCurrency("EUR")
                .build());
    }
}
