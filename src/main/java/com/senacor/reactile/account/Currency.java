package com.senacor.reactile.account;

import com.senacor.reactile.ValueObject;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject
public class Currency implements ValueObject {

    private final String currency;

    public Currency() {
        this((String) null);
    }

    public Currency(String currency) {
        this.currency = currency;
    }

    public Currency(JsonObject jsonObject) {
        this(jsonObject.getString("id"));
    }

    public Currency(Currency currency) {
        this(currency.getCurrency());
    }

    public JsonObject toJson(Currency currency) {
        return new JsonObject().put("currency", currency);
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toValue() {
        return currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Currency other = (Currency) obj;
        return Objects.equals(this.currency, other.currency);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currency='" + currency + '\'' +
                '}';
    }
}
