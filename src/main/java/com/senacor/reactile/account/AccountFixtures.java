package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;
import rx.functions.Func2;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

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

    public static Account randomAccount(String accountId) {
        return randomAccount()
                .withId(accountId)
                .build();
    }


    public static Account randomAccount(CustomerId customerId) {
        return randomAccount()
                .withCustomerId(customerId)
                .build();
    }

    public static Account randomAccount(String accountId, String customerId) {
        return randomAccount()
                .withId(accountId)
                .withCustomerId(customerId)
                .build();
    }

    public static Account.Builder randomAccount() {
        return Account.anAccount()
                .withId("acc-" + uuid())
                .withCustomerId("cust-" + uuid())
                .withBalance(new BigDecimal(rd.nextInt(4000)))
                .withCurrency("EUR" );
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static Observable<Account> randomAccounts(int count) {
        return randomAccounts(customerId(count));
    }

    public static Observable<Account> randomAccounts(Observable<CustomerId> customers) {
        return customers
                .flatMap(customerId -> zip(accountId(customerId), balance(), zipToAccount(customerId)));
    }

    private static Observable<CustomerId> customerId(int count) {
        return Observable.range(500_000_000, count).map(i -> new CustomerId("" + i));
    }

    private static Observable<AccountId> accountId(CustomerId customerId) {
        return random(4).map(accId -> new AccountId(customerId.getId() + "-ac-" + accId));
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

    private static Observable<Integer> random(int bound) {
        return Observable.range(1, rd.nextInt(bound) + 1);
    }

}
