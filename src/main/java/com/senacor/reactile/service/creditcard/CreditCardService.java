package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

import java.util.List;

import static java.util.stream.Collectors.toList;

@ProxyGen
public interface CreditCardService {
    String ADDRESS = "CreditCardService";

    @GenIgnore
    default Observable<CreditCard> getCreditCard(CreditCardId creditCardId) {
        ObservableFuture<CreditCard> observableFuture = RxHelper.observableFuture();
        getCreditCard(creditCardId, observableFuture.toHandler());
        return observableFuture;
    }

    @GenIgnore
    default Observable<List<CreditCard>> getCreditCardsForCustomer(CustomerId customerId){
        ObservableFuture<List<JsonObject>> observableFuture = RxHelper.observableFuture();
        getCreditCardsForCustomer(customerId, observableFuture.toHandler());
        return observableFuture.map(list -> list.stream().map(CreditCard::fromJson).collect(toList()));
    }

    @GenIgnore
    default Observable<CreditCard> createCreditCard(CreditCard creditCard){
        ObservableFuture<String> observableFuture = RxHelper.observableFuture();
        createCreditCard(creditCard, observableFuture.toHandler());
        return observableFuture.flatMap(id -> Observable.just(creditCard));
    }

    void getCreditCard(CreditCardId creditCardId, Handler<AsyncResult<CreditCard>> resultHandler);

    void getCreditCardsForCustomer(CustomerId customerId, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    void createCreditCard(CreditCard creditCard, Handler<AsyncResult<String>> resultHandler);


}
