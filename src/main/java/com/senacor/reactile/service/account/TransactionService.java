package com.senacor.reactile.service.account;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

import java.util.List;

public interface TransactionService {

    @Action(returnType = TransactionList.class)
    public Observable<TransactionList> getTransactionsForCustomer(CustomerId customerId);

    @Action(returnType = TransactionList.class)
    public Observable<TransactionList> getTransactionsForAccount(AccountId accountId);

    @Action(returnType = TransactionList.class)
    public Observable<TransactionList> getTransactionsForCreditCard(CreditCardId creditCardId);

    @Action(returnType = Transaction.class)
    public Observable<Transaction> createTransaction(Transaction transaction);
}
