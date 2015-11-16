package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.service.customer.CustomerId;
import rx.Observable;

public interface CreditCardService {

    @Action(returnType = CreditCard.class)
    public Observable<CreditCard> getCreditCard(CreditCardId creditCardId);

    @Action(returnType = CreditCardList.class)
    public Observable<CreditCardList> getCreditCardsForCustomer(CustomerId customerId);

    @Action(returnType = CreditCard.class)
    public Observable<CreditCard> createCreditCard(CreditCard creditCard);
}
