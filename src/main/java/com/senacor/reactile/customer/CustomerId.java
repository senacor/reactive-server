package com.senacor.reactile.customer;

public class CustomerId {

    private final String id;

    public CustomerId(String id) {
        checkArgument(id != null);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
