package com.senacor.reactile.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.Identity;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.domain.Amount;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@DataObject
public class CreditCard implements Product, Jsonizable, Identity<CreditCardId> {
    private final CreditCardId id;
    private final CustomerId customerId;
    private final Amount balance;

    public CreditCard() {
        this(null, null, null);
    }

    public CreditCard(
            @JsonProperty("id") CreditCardId id,
            @JsonProperty("customerId") CustomerId customerId,
            @JsonProperty("balance") Amount balance) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
    }


    public CreditCard(CreditCard creditCard) {
        this(creditCard.getId(), creditCard.getCustomerId(), creditCard.getBalance());
    }


    public CreditCard(JsonObject jsonObject) {
        this(
                new CreditCardId(jsonObject.getString("id")),
                new CustomerId(jsonObject.getString("customerId")),
                Amount.fromJson(jsonObject.getJsonObject("balance"))
        );
    }


    public static CreditCard fromJson(JsonObject jsonObject) {
        checkArgument(jsonObject != null);
        return aCreditCard()
                .withId(jsonObject.getString("id"))
                .withCustomerId(new CustomerId(jsonObject.getString("customerId")))
                .withBalance(Amount.fromJson(jsonObject.getJsonObject("balance")))
                .build();
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.getId())
                .put("customerId", customerId.getId())
                .put("balance", balance.toJson());
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

    @Override
    public Type getType() {
        return Type.CREDITCARD;
    }

    public Amount getBalance() {
        return balance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, balance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CreditCard other = (CreditCard) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.customerId, other.customerId)
                && Objects.equals(this.balance, other.balance);
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", balance=" + balance +
                '}';
    }

    public static final class Builder {
        private CreditCardId id;
        private CustomerId customerId;
        private Amount balance;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = new CreditCardId(id);
            return this;
        }

        public Builder withId(CreditCardId id) {
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
            this.balance = new Amount(balance);
            return this;
        }

        public Builder withBalance(Amount balance) {
            this.balance = balance;
            return this;
        }

        public CreditCard build() {
            return new CreditCard(id, customerId, balance);
        }

    }
}
