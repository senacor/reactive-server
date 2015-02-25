package com.senacor.reactile.customer;

import com.senacor.reactile.IdObject;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

public class CustomerId implements IdObject {

    private final String id;

    public CustomerId(String id) {
        checkArgument(id != null);
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
        final CustomerId other = (CustomerId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "CustomerId{" +
                "id='" + id + '\'' +
                '}';
    }

}
