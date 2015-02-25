package com.senacor.reactile.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private final CustomerId id;

    private final List<Address> addresses;

    private final List<Contact> contacts;

    private final Country taxCountry;

    private final String taxNumber;

    public Customer(
            @JsonProperty("id") CustomerId id,
            @JsonProperty("addresses") List<Address> addresses,
            @JsonProperty("contacts") List<Contact> contacts,
            @JsonProperty("taxNumber") String taxNumber,
            @JsonProperty("taxCountry") Country taxCountry) {
        this.id = id;
        this.addresses = addresses;
        this.contacts = contacts;
        this.taxNumber = taxNumber;
        this.taxCountry = taxCountry;
    }

    public static Builder newBuilder() {
        return new Builder();
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


    public static final class Builder {
        private CustomerId id;
        private List<Address> addresses = new ArrayList<>();
        private List<Contact> contacts = new ArrayList<>();
        private Country taxCountry;
        private String taxNumber;

        private Builder() {
        }

        public Builder withId(CustomerId id) {
            this.id = id;
            return this;
        }

        public Builder withAddresses(List<Address> addresses) {
            this.addresses = addresses;
            return this;
        }

        public Builder withContacts(List<Contact> contacts) {
            this.contacts = contacts;
            return this;
        }

        public Builder withTaxCountry(Country taxCountry) {
            this.taxCountry = taxCountry;
            return this;
        }

        public Builder withTaxNumber(String taxNumber) {
            this.taxNumber = taxNumber;
            return this;
        }

        public Customer build() {
            return new Customer(id, addresses, contacts, taxNumber, taxCountry);
        }

        public Builder withId(String id) {
            return withId(new CustomerId(id));
        }
    }
}
