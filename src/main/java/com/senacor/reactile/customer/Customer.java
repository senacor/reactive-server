package com.senacor.reactile.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.Identity;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshal;

@DataObject
public class Customer implements Identity<CustomerId>, Jsonizable {

    private final CustomerId id;

    private final String firstname;

    private final String lastname;

    private final List<Address> addresses;

    private final List<Contact> contacts;

    private final Country taxCountry;

    private final String taxNumber;

    public Customer() {
        this(null, null, null, null, null, null, null);
    }

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

    public Customer(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public Customer(Customer customer) {
        this(
                customer.getId(),
                customer.getFirstname(),
                customer.getLastname(),
                customer.getAddresses(),
                customer.getContacts(),
                customer.getTaxNumber(),
                customer.getTaxCountry());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public CustomerId getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
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

    public List<Contact> getContacts() {
        return contacts;
    }

    public static Customer fromJson(JsonObject jsonObject) {
        checkArgument(jsonObject != null);
        return Customer.newBuilder()
                .withId(jsonObject.getString("id"))
                .withFirstname(jsonObject.getString("firstname"))
                .withLastname(jsonObject.getString("lastname"))
                .withTaxNumber(jsonObject.getString("taxnumber"))
                .withTaxCountry(Country.fromJson(jsonObject.getJsonObject("taxCountry")))
                .withAddresses(unmarshal(jsonObject.getJsonArray("addresses"), Address::fromJson))
                .withContacts(unmarshal(jsonObject.getJsonArray("contacts"), Contact::fromJson))
                .build();
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.getId())
                .put("firstname", firstname)
                .put("lastname", lastname)
                .put("addresses", marshal(addresses, Address::toJson))
                .put("contacts", marshal(contacts, Contact::toJson))
                .put("taxCountry", null == taxCountry ? null : taxCountry.toJson())
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
