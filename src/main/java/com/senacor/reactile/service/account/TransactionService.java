package com.senacor.reactile.service.account;

import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import rx.Observable;

import java.util.List;

public interface TransactionService {

    Observable<List<Transaction>> getTransactionsForCustomer(CustomerId customerId);
    Observable<List<Transaction>> getTransactionsForAccount(AccountId accountId);
    Observable<List<Transaction>> getTransactionsForCreditCard(CreditCardId creditCardId);

    Observable<Transaction> createTransaction(Transaction transaction);
}
