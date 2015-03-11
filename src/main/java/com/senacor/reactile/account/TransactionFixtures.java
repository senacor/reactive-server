package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;
import rx.functions.Func3;

import java.math.BigDecimal;
import java.util.Random;

import static com.senacor.reactile.account.Transaction.aTransaction;
import static rx.Observable.just;
import static rx.Observable.zip;

public final class TransactionFixtures {
    private final static Random rd = new Random();

    private TransactionFixtures() {
    }

    public static Observable<Transaction> randomTransactions(int count) {
        return randomTransactions(customerId(count));
    }

    public static Observable<Transaction> randomTransactions(Observable<CustomerId> customers) {
        return customers.flatMap(customerId -> {
            Observable<Transaction> accountTransactions = accountId(customerId).flatMap(accountId -> zip(transactionId(accountId), just(customerId), balance(), zipToAccTransaction(accountId)));
            Observable<Transaction> creditCardTransactions = creditCardId(customerId).flatMap(creditCardId -> zip(transactionId(creditCardId), just(customerId), balance(), zipToCcTransaction(creditCardId)));
            return Observable.merge(accountTransactions, creditCardTransactions);
        });

    }

    private static Observable<CustomerId> customerId(int count) {
        return Observable.range(500_000_000, count).map(i -> new CustomerId("" + i));
    }

    private static Observable<AccountId> accountId(CustomerId customerId) {
        return random(4).map(accId -> new AccountId(customerId.getId() + "-ac-" + accId));
    }


    private static Observable<CreditCardId> creditCardId(CustomerId customerId) {
        return random(4).map(ccId -> new CreditCardId(customerId.getId() + "-cc-" + ccId));
    }

    private static Observable<BigDecimal> balance() {
        return just(new BigDecimal(rd.nextInt(10000) - 5000));
    }

    private static Observable<TransactionId> transactionId(AccountId accountId) {
        return random(30).map(txId -> new TransactionId(accountId.getId() + "-tx-" + txId));
    }

    private static Observable<TransactionId> transactionId(CreditCardId creditCardId) {
        return random(30).map(txId -> new TransactionId(creditCardId.getId() + "-tx-" + txId));
    }

    private static Func3<TransactionId, CustomerId, BigDecimal, Transaction> zipToAccTransaction(AccountId accountId) {
        return (transactionId, customerId, amount) ->
                aTransaction()
                        .withId(transactionId)
                        .withCustomerId(customerId.getId())
                        .withAccountId(accountId.getId())
                        .withAmount(amount)
                        .withCurrency("EUR")
                        .build();
    }

    private static Func3<TransactionId, CustomerId, BigDecimal, Transaction> zipToCcTransaction(CreditCardId creditCardId) {
        return (transactionId, customerId, amount) ->
                aTransaction()
                        .withId(transactionId)
                        .withCustomerId(customerId)
                        .withCreditCardId(creditCardId.getId())
                        .withAmount(amount)
                        .withCurrency("EUR")
                        .build();
    }

    private static Observable<Integer> random(int bound) {
        return Observable.range(1, rd.nextInt(bound) + 1);
    }
}
