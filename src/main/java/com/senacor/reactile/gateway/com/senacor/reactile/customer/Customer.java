package com.senacor.reactile.gateway.com.senacor.reactile.customer;

import java.util.List;

public class Customer {

    private final CustomerId id;

    private List<Address> addresses;

    private Country taxCountry;

    private String taxNumber;

    public Customer(CustomerId id) {
        this.id = id;
    }

    public CustomerId getId() {
        return id;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public Country getTaxCountry() {
        return taxCountry;
    }

    public String getTaxNumber() {
        return taxNumber;
    }
}
