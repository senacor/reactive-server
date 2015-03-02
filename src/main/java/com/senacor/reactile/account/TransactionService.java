package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;

public interface TransactionService {

    Observable<Transaction> getTransaction(CustomerId customerId);
    Observable<Transaction> getTransaction(CustomerId customerId, AccountId accountId);
    Observable<Transaction> getTransaction(CustomerId customerId, CreditCardId creditCardId);
}
