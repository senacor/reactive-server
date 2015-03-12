package com.senacor.reactile.account;

import com.senacor.reactile.ValueObject;

public class Currency implements ValueObject {

    private final String currency;

    public Currency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toValue() {
        return currency;
    }
}
