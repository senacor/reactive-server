package com.senacor.reactile.customer;

public class Address {

    private final String coHint;
    private final String street;
    private final String zipCode;
    private final String addressNumber;
    private final String city;
    private final Country country;


    public Address(String coHint, String street, String zipCode, String addressNumber, String city, Country country) {
        this.coHint = coHint;
        this.street = street;
        this.zipCode = zipCode;
        this.addressNumber = addressNumber;
        this.city = city;
        this.country = country;
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
}
