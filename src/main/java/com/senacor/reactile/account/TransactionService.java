package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;

import java.util.List;

public interface TransactionService {

    Observable<List<Transaction>> getTransactionsForCustomer(CustomerId customerId);
    Observable<List<Transaction>> getTransactionsForAccount(AccountId accountId);
    Observable<List<Transaction>> getTransactionsForCreditCard(CreditCardId creditCardId);

    Observable<Transaction> createTransaction(Transaction transaction);
}
