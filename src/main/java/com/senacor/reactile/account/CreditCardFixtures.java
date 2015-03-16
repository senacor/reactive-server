package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;
import rx.functions.Func2;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import static rx.Observable.just;
import static rx.Observable.zip;

public final class CreditCardFixtures {
    private final static Random rd = new Random();

    private CreditCardFixtures() {
    }

    public static CreditCard newCreditCardWithCustomer(String customerId) {
        return defaultCreditCard()
                .withCustomerId(customerId)
                .build();
    }

    public static CreditCard newCreditCard(String id) {
        return randomCreditCardBuilder()
                .withId(id)
                .build();
    }

    public static CreditCard randomCreditCard(CustomerId customerId) {
        return randomCreditCardBuilder().withCustomerId(customerId).build();
    }

    public static CreditCard randomCreditCard(CreditCardId id, CustomerId customerId) {
        return randomCreditCardBuilder().withCustomerId(customerId).withId(id).build();
    }

    public static CreditCard randomCreditCard(String id, String customerId) {
        return randomCreditCard(new CreditCardId(id), new CustomerId(customerId));
    }

    public static CreditCard.Builder defaultCreditCard() {
        return CreditCard.aCreditCard()
                .withId("000000000000456")
                .withCustomerId("cust-0815")
                .withBalance(new BigDecimal("-1000"))
                .withCurrency("EUR");
    }

    private static CreditCard.Builder randomCreditCardBuilder() {
        return defaultCreditCard()
                .withId("cc-" + uuid())
                .withCustomerId("cust-" + uuid())
                .withBalance(BigDecimal.valueOf(rd.nextDouble()))
                .withCurrency("EUR");
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static Observable<CreditCard> randomCreditCards(Observable<CustomerId> customers) {
        return customers
                .flatMap(customerId -> zip(creditCardId(customerId), balance(), zipToCreditCard(customerId)));
    }

    public static Observable<CreditCard> randomCreditCards(int count) {
        return randomCreditCards(customerId(count));
    }

    private static Observable<CustomerId> customerId(int count) {
        return Observable.range(500_000_000, count).map(i -> new CustomerId("" + i));
    }


    private static Observable<CreditCardId> creditCardId(CustomerId customerId) {
        return random(4).map(ccId -> new CreditCardId(customerId.getId() + "-cc-" + ccId));
    }

    private static Observable<BigDecimal> balance() {
        return just(new BigDecimal(rd.nextInt(10000) - 5000));
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
