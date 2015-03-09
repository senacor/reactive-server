package com.senacor.reactile.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private final CustomerId id;

    private final String firstname;

    private final String lastname;

    private final List<Address> addresses;

    private final List<Contact> contacts;

    private final Country taxCountry;

    private final String taxNumber;

    public Customer(
            @JsonProperty("id") CustomerId id,
            @JsonProperty("firstname") String firstname,
            @JsonProperty("lastname") String lastname,
            @JsonProperty("addresses") List<Address> addresses,
            @JsonProperty("contacts") List<Contact> contacts,
            @JsonProperty("taxNumber") String taxNumber,
            @JsonProperty("taxCountry") Country taxCountry) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
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

    public String getFirstname() { return firstname; }

    public String getLastname() { return lastname; }

    public List<Address> getAddresses() {
        return addresses;
    }

    public Country getTaxCountry() {
        return taxCountry;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public static Customer fromJson(JsonObject jsonObject) {
        System.out.println("jsonObject.encodePrettily() = " + jsonObject.encodePrettily());

        Customer cust = Customer.newBuilder()
                .withId(jsonObject.getString("id"))
                .withFirstname(jsonObject.getString("firstname"))
                .withLastname(jsonObject.getString("lastname"))
                .withTaxNumber(jsonObject.getString("taxNumber"))
                .withTaxCountry(Country.fromJson(jsonObject.getJsonObject("taxCountry"))).build();

        JsonArray ads = jsonObject.getJsonArray("addresses");
        for (Object ad: ads) {
            JsonObject jsonAd = (JsonObject) ad;
            cust.addresses.add(Address.fromJson(jsonAd));
        }

        JsonArray cons = jsonObject.getJsonArray("contacts");
        for (Object con: cons) {
            JsonObject jsonCon = (JsonObject) con;
            cust.contacts.add(Contact.fromJson(jsonCon));
        }

        return cust;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.getId())
                .put("firstname", firstname)
                .put("lastname", lastname)
                .put("addresses", new JsonArray(addresses))
                .put("contacts", new JsonArray(contacts))
                .put("taxCountry", taxCountry.toJson())
                .put("taxnumber", taxNumber);
    }



    public static final class Builder {
        private CustomerId id;
        private String firstname;
        private String lastname;
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

        public Builder withFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder withLastname(String lastname) {
            this.lastname = lastname;
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
            return new Customer(id, firstname, lastname, addresses, contacts, taxNumber, taxCountry);
        }

        public Builder withId(String id) {
            return withId(new CustomerId(id));
        }
    }
}
