package com.senacor.reactile.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.customer.CustomerId;

import java.math.BigDecimal;

public class CreditCard {
    private final CreditCardId id;
    private final CustomerId customerId;
    private final BigDecimal balance;
    private final Currency currency;

    public CreditCard(
            @JsonProperty("id") CreditCardId id,
            @JsonProperty("customerId") CustomerId customerId,
            @JsonProperty("balance") BigDecimal balance,
            @JsonProperty("currency") Currency currency) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        this.currency = currency;
    }

    public static Builder aCreditCard() {
        return new Builder();
    }

    public CreditCardId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", balance=" + balance +
                ", currency=" + currency +
                '}';
    }

    public static final class Builder {
        private CreditCardId id;
        private CustomerId customerId;
        private BigDecimal balance;
        private Currency currency;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = new CreditCardId(id);
            return this;
        }

        public Builder withCustomerId(String customerId) {
            this.customerId = new CustomerId(customerId);
            return this;
        }

        public Builder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder withCurrency(String currency) {
            this.currency = new Currency(currency);
            return this;
        }

        public CreditCard build() {
            return new CreditCard(id, customerId, balance, currency);
        }

    }
}
