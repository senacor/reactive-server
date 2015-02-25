package com.senacor.reactile.account;

import com.senacor.reactile.IdObject;

import java.util.Objects;

public class AccountId implements IdObject {

    private final String id;

    public AccountId(String id) {
        this.id = id;
    }

    @Override
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
        final AccountId other = (AccountId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "AccountId{" +
                "id='" + id + '\'' +
                '}';
    }
}
