package com.senacor.reactile.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.Identity;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;

public class Account implements Product, Jsonizable, Identity<AccountId> {
    private final AccountId id;
    private final CustomerId customerId;
    private final BigDecimal balance;
    private final Currency currency;

    public Account(
            @JsonProperty("id") AccountId id,
            @JsonProperty("customerId") CustomerId customerId,
            @JsonProperty("balance") BigDecimal balance,
            @JsonProperty("currency") Currency currency) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        this.currency = currency;
    }

    public static Builder anAccount() {
        return new Builder();
    }

    public AccountId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    @Override
    public Type getType() {
        return Type.ACCOUNT;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", balance=" + balance +
                ", currency=" + currency +
                '}';
    }

    public static Account fromJson(JsonObject jsonObject) {
        return anAccount()
                .withId(jsonObject.getString("id"))
                .withCustomerId(new CustomerId(jsonObject.getString("customerId")))
                .withBalance(new BigDecimal(jsonObject.getString("balance")))
                .withCurrency(jsonObject.getString("currency"))
                .build();
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.getId())
                .put("customerId", customerId.getId())
                .put("balance", balance.toString())
                .put("currency", currency.getCurrency());
    }

    public static final class Builder {
        private AccountId id;
        private CustomerId customerId;
        private BigDecimal balance;
        private Currency currency;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = new AccountId(id);
            return this;
        }

        public Builder withId(AccountId id) {
            this.id = id;
            return this;
        }

        public Builder withCustomerId(CustomerId customerId) {
            this.customerId = customerId;
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

        public Account build() {
            return new Account(id, customerId, balance, currency);
        }

    }
}
