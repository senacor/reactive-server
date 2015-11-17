package com.senacor.reactile.service.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.domain.Amount;
import com.senacor.reactile.domain.Identity;
import com.senacor.reactile.json.Jsonizable;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;

@DataObject
public class Account implements Product, Jsonizable, Identity<AccountId> {
    private final AccountId id;
    private final CustomerId customerId;
    private final Amount balance;

    public Account() {
        this(null, null, null);
    }

    public Account(
            @JsonProperty("id") AccountId id,
            @JsonProperty("customerId") CustomerId customerId,
            @JsonProperty("balance") Amount balance) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
    }

    public Account(Account account) {
        this(account.getId(), account.getCustomerId(), account.getBalance());
    }

    public Account(JsonObject jsonObject) {
        this(fromJson(jsonObject));
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

    public Amount getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", balance=" + balance +
                '}';
    }

    public static Account fromJson(JsonObject jsonObject) {
        return anAccount()
                .withId(jsonObject.getString("id"))
                .withCustomerId(new CustomerId(jsonObject.getString("customerId")))
                .withBalance(Amount.fromJson(jsonObject.getJsonObject("balance")))
                .build();
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.getId())
                .put("customerId", customerId.getId())
                .put("balance", balance.toJson());
    }

    public static final class Builder {
        private AccountId id;
        private CustomerId customerId;
        private Amount balance;

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

        public Builder withBalance(Amount balance) {
            this.balance = balance;
            return this;
        }
        public Builder withBalance(BigDecimal balance) {
            this.balance = new Amount(balance);
            return this;
        }

        public Account build() {
            return new Account(id, customerId, balance);
        }

    }
}
