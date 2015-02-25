package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;

public interface CreditCardService {

    Observable<CreditCard> getCreditCard(CustomerId customerId);
}
