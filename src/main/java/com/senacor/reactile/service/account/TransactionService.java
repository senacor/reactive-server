package com.senacor.reactile.service.account;

import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

import java.util.List;

@ProxyGen
@VertxGen
public interface TransactionService {
    String ADDRESS = "TransactionService";

    void getTransactionsForCustomer(CustomerId customerId, Handler<AsyncResult<TransactionList>> resultHandler);
    void getTransactionsForAccount(AccountId accountId, Handler<AsyncResult<TransactionList>> resultHandler);
    void getTransactionsForCreditCard(CreditCardId creditCardId, Handler<AsyncResult<TransactionList>> resultHandler);

    void createTransaction(Transaction transaction, Handler<AsyncResult<Transaction>> resultHandler);
}
