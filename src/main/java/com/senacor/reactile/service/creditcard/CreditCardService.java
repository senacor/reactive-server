package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

import java.util.List;

import static java.util.stream.Collectors.toList;

@ProxyGen
@VertxGen
public interface CreditCardService {
    String ADDRESS = "CreditCardService";


    void getCreditCard(CreditCardId creditCardId, Handler<AsyncResult<CreditCard>> resultHandler);

    void getCreditCardsForCustomer(CustomerId customerId, Handler<AsyncResult<CreditCardList>> resultHandler);

    void createCreditCard(CreditCard creditCard, Handler<AsyncResult<CreditCard>> resultHandler);


}
