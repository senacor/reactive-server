package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;

import java.util.List;

public interface CreditCardService {

    Observable<CreditCard> getCreditCard(CreditCardId creditCardId);
    Observable<List<CreditCard>> getCreditCardsForCustomer(CustomerId customerId);

    Observable<CreditCard> createCreditCard(CreditCard creditCard);
}
