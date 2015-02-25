package com.senacor.reactile.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CreditCardId {

    private final String id;

    public CreditCardId(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CreditCardId other = (CreditCardId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "CreditCardId{" +
                "id='" + id + '\'' +
                '}';
    }
}
