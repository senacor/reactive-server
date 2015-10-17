package com.senacor.reactile.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@DataObject
public class Amount implements Jsonizable{

    private final BigDecimal value;
    private final String currency;

    public Amount() {
        this(null, null);
    }

    public Amount(BigDecimal value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public Amount(BigDecimal value) {
        this(value, "EUR");
    }

    public Amount(Amount amount) {
        this(amount.value, amount.currency);
    }

    public Amount(JsonObject jsonObject) {
        this(new BigDecimal(jsonObject.getDouble("value")), jsonObject.getString("currency"));
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("value", value.setScale(2, RoundingMode.CEILING).doubleValue())
                .put("currency", currency);
    }

    public static Amount fromJson(JsonObject jsonObject) {
        return new Amount(new BigDecimal(jsonObject.getDouble("value")), jsonObject.getString("currency"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Amount other = (Amount) obj;
        return Objects.equals(this.value, other.value)
                && Objects.equals(this.currency, other.currency);
    }

    @Override
    public String toString() {
        return "Amount{" +
                "value=" + value +
                ", currency='" + currency + '\'' +
                '}';
    }
}
