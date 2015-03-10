package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;

import java.math.BigDecimal;

public final class AccountFixtures {

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
}
