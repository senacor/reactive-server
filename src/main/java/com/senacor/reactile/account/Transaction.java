package com.senacor.reactile.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;

/**
 * Created by rwinzing on 02.03.15.
 */
public class Transaction implements Jsonizable {
    private final TransactionId id;
    private final CustomerId customerId;
    private final AccountId accountId;
    private final CreditCardId creditCardId;
    private final BigDecimal amount;
    private final Currency currency;

    public Transaction(
            @JsonProperty("id") TransactionId id,
            @JsonProperty("customerId") CustomerId customerId,
            @JsonProperty("accountId") AccountId accountId,
            @JsonProperty("creditCardId") CreditCardId creditCardId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") Currency currency) {
        this.id = id;
        this.customerId = customerId;
        this.accountId = accountId;
        this.creditCardId = creditCardId;
        this.amount = amount;
        this.currency = currency;
    }

    public static Builder aTransaction() {
        return new Builder();
    }

    public TransactionId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public CreditCardId getCreditCardId() {
        return creditCardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static Transaction fromJson(JsonObject jsonObject) {
        return aTransaction()
                .withId(jsonObject.getString("id"))
                .withCustomerId(jsonObject.getString("customerId"))
                .withAccountId(jsonObject.getString("accountId"))
                .withCreditCardId(jsonObject.getString("creditCardId"))
                .withAmount(new BigDecimal(jsonObject.getString("amount")))
                .withCurrency(jsonObject.getString("currency"))
                .build();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.put("id", id.getId());
        json.put("customerId", customerId.getId());
        if (accountId != null) {
            json.put("accountId", accountId.getId());
        } else {
            json.put("creditCardId", creditCardId.getId());
        }
        json.put("amount", amount.toString());
        json.put("currency", currency.getCurrency());

        return json;
    }
    public static final class Builder {
        private TransactionId id;
        private CustomerId customerId;
        private AccountId accountId;
        private CreditCardId creditCardId;
        private BigDecimal amount;
        private Currency currency = new Currency("EUR");

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = new TransactionId(id);
            return this;
        }

        public Builder withId(TransactionId id) {
            this.id = id;
            return this;
        }

        public Builder withCustomerId(String customerId) {
            this.customerId = new CustomerId(customerId);
            return this;
        }

        public Builder withCustomerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withAccountId(String accountId) {
            this.accountId = new AccountId(accountId);
            return this;
        }

        public Builder withCreditCardId(String creditCardId) {
            this.creditCardId = new CreditCardId(creditCardId);
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCurrency(String currency) {
            this.currency = new Currency(currency);
            return this;
        }

        public Transaction build() {
            return new Transaction(id, customerId, accountId, creditCardId, amount, currency);
        }
    }
}
