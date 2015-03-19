package com.senacor.reactile.customer;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Address implements Jsonizable {

    private final String coHint;
    private final String street;
    private final String zipCode;
    private final String addressNumber;
    private final String city;
    private final Country country;

    public Address() {
        this.coHint = "";
        this.street = "";
        this.zipCode = "";
        this.addressNumber = "";
        this.city = "";
        this.country = new Country();
    }

    public Address(String coHint, String street, String zipCode, String addressNumber, String city, Country country) {
        this.coHint = coHint;
        this.street = street;
        this.zipCode = zipCode;
        this.addressNumber = addressNumber;
        this.city = city;
        this.country = country;
    }

    public Address(Address address) {
        this (address.coHint, address.street, address.zipCode, address.addressNumber, address.city, (address.country!=null)?new Country(address.country):new Country());
    }
    public Address(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    private Address(Builder builder) {
        coHint = builder.coHint;
        street = builder.street;
        zipCode = builder.zipCode;
        addressNumber = builder.addressNumber;
        city = builder.city;
        country = builder.country;
    }

    public static Builder anAddress() {
        return new Builder();
    }

    public String getCoHint() {
        return coHint;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public String getCity() {
        return city;
    }

    public Country getCountry() {
        return country;
    }


    public JsonObject toJson() {
        return new JsonObject()
                .put("coHint", coHint)
                .put("street", street)
                .put("zipCode", zipCode)
                .put("addressNumber", addressNumber)
                .put("city", city);
    }

    public static Address fromJson(JsonObject jsonObject) {
        return Address.anAddress()
                .withCoHint(jsonObject.getString("coHint"))
                .withStreet(jsonObject.getString("street"))
                .withZipCode(jsonObject.getString("zipCode"))
                .withAddressNumber(jsonObject.getString("addressNumber"))
                .withCity(jsonObject.getString("city")).build();
    }

    public static final class Builder {
        private String coHint;
        private String street;
        private String zipCode;
        private String addressNumber;
        private String city;
        private Country country;

        private Builder() {
        }

        public Builder withCoHint(String coHint) {
            this.coHint = coHint;
            return this;
        }

        public Builder withStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder withAddressNumber(String addressNumber) {
            this.addressNumber = addressNumber;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withCountry(Country country) {
            this.country = country;
            return this;
        }

        public Address build() {
            return new Address(coHint, street, zipCode, addressNumber, city, country);
        }
    }
}
