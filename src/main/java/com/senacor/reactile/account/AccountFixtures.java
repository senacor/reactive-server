package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;
import rx.functions.Func2;

import java.math.BigDecimal;
import java.util.Random;

import static rx.Observable.just;
import static rx.Observable.zip;

public final class AccountFixtures {
    private final static Random rd = new Random();

    private AccountFixtures() {
    }

    public static Account newAccount2() {
        return Account.anAccount()
                .withId("08-cust-15-ac-2")
                .withCustomerId(new CustomerId("08-cust-15"))
                .withBalance(new BigDecimal("20773"))
                .withCurrency("EUR")
                .build();
    }

    public static Account newAccount1() {
        return Account.anAccount()
                .withId("08-cust-15-ac-1")
                .withCustomerId(new CustomerId("08-cust-15"))
                .withBalance(new BigDecimal("18773"))
                .withCurrency("EUR")
                .build();
    }

    public static Observable<Account> randomAccounts(int count) {
        return randomAccounts(customerId(count));
    }

    public static Observable<Account> randomAccounts(Observable<CustomerId> customers) {
        return customers
                .flatMap(customerId -> zip(accountId(customerId), balance(), zipToAccount(customerId)));
    }

    public static Observable<CreditCard> randomCreditCards(int count) {
        return randomCreditCards(customerId(count));
    }

    public static Observable<CreditCard> randomCreditCards(Observable<CustomerId> customers) {
        return customers
                .flatMap(customerId -> zip(creditCardId(customerId), balance(), zipToCreditCard(customerId)));
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

    private static Func2<AccountId, BigDecimal, Account> zipToAccount(CustomerId customerId) {
        return (accountId, amount) -> Account.anAccount()
                .withId(accountId)
                .withCustomerId(customerId)
                .withBalance(amount)
                .withCurrency("EUR")
                .build();
    }

    private static Func2<CreditCardId, BigDecimal, CreditCard> zipToCreditCard(CustomerId customerId) {
        return (ccId, amount) -> CreditCard.aCreditCard()
                .withId(ccId)
                .withCustomerId(customerId)
                .withBalance(amount)
                .withCurrency("EUR")
                .build();
    }

    private static Observable<Integer> random(int bound) {
        return Observable.range(1, rd.nextInt(bound) + 1);
    }
}
