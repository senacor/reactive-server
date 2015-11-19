package com.senacor.reactile.service.customer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.json.Jsonizable;

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
    /**
     * identifier of Address within Customer
     */
    private final Integer index;

    public Address() {
        this(null, null, null, null, null, null, null);
    }

    public Address(@JsonProperty("coHint") String coHint,
    		@JsonProperty("street") String street,
    		@JsonProperty("zipCode") String zipCode,
    		@JsonProperty("addressNumber") String addressNumber,
    		@JsonProperty("city") String city,
    		@JsonProperty("country") Country country,
    		@JsonProperty("index") Integer index) {
        this.coHint = coHint;
        this.street = street;
        this.zipCode = zipCode;
        this.addressNumber = addressNumber;
        this.city = city;
        this.country = country;
        this.index = index;
    }

    public Address(Address address) {
        this(address.coHint,
                address.street,
                address.zipCode,
                address.addressNumber,
                address.city,
                (address.country != null) ? new Country(address.country) : new Country(),
                address.index);
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
        index = builder.index;
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

    public Integer getIndex() {
        return index;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("coHint", coHint)
                .put("street", street)
                .put("zipCode", zipCode)
                .put("addressNumber", addressNumber)
                .put("city", city)
                .put("country", null == country ? null : country.toJson())
                .put("index", index);
    }

    public static Address fromJson(JsonObject jsonObject) {
        return null == jsonObject ? null : Address.anAddress()
                .withCoHint(jsonObject.getString("coHint"))
                .withStreet(jsonObject.getString("street"))
                .withZipCode(jsonObject.getString("zipCode"))
                .withAddressNumber(jsonObject.getString("addressNumber"))
                .withCity(jsonObject.getString("city"))
                .withCountry(Country.fromJson(jsonObject.getJsonObject("country")))
                .withIndex(jsonObject.getInteger("index"))
                .build();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static final class Builder {
        private String coHint;
        private String street;
        private String zipCode;
        private String addressNumber;
        private String city;
        private Country country;
        private Integer index;

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

        public Builder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public Builder withAddress(Address address) {
            withCoHint(address.coHint)
                    .withStreet(address.street)
                    .withZipCode(address.zipCode)
                    .withAddressNumber(address.addressNumber)
                    .withCity(address.city)
                    .withCountry(address.country)
                    .withIndex(address.index);

            return this;
        }

        public Address build() {
            return new Address(coHint, street, zipCode, addressNumber, city, country, index);
        }
    }
}
