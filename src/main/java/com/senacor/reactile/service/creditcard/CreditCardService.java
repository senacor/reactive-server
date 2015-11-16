package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.service.customer.Customer;
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

public interface CreditCardService {

    @Action(returnType = CreditCard.class)
    public Observable<CreditCard> getCreditCard(CreditCardId creditCardId);

    @Action(returnType = CreditCardList.class)
    public Observable<CreditCardList> getCreditCardsForCustomer(CustomerId customerId);

    @Action(returnType = CreditCard.class)
    public Observable<CreditCard> createCreditCard(CreditCard creditCard);
}
