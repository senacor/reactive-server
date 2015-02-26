package com.senacor.reactile.customer;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by rwinzing on 25.02.15.
 */
public class CustomerFromToJsonTest {
    @Test
    public void thatValidJsonIsGenerated() {
        Customer customer = getSampleCustomer();

        JsonObject ref = new JsonObject(getSampleCustomerJson());
        assertThat(customer.toJson(), is(equalTo(ref)));
    }

    @Test
    public void thatJsonCanBeParsed() {
        String json = getSampleCustomerJson();

        Customer customer = Customer.fromJson(new JsonObject(json));
        Customer refCustomer = getSampleCustomer();

        assertThat(customer, is(equalTo(refCustomer)));
    }

    private Customer getSampleCustomer() {
        List<Address> addresses = new ArrayList<>();
        addresses.add(Address.anAddress()
                        .withAddressNumber("1")
                        .withCoHint("c/o Mustermann")
                        .withStreet("Winterstrasse")
                        .withCity("Sommerdorf")
                        .withZipCode("12345")
                        .withCountry(new Country("Deutschland", "DE")).build()
        );

        Customer customer = Customer.newBuilder()
                .withId(new CustomerId("08-cust-15"))
                .withAddresses(addresses)
                .withTaxCountry(new Country("England", "EN"))
                .withTaxNumber("47-tax-11").build();

        return customer;
    }

    private String getSampleCustomerJson() {
        String json = "{\n" +
                "  \"taxCountry\" : {\n" +
                "    \"name\" : \"England\",\n" +
                "    \"code\" : \"EN\"\n" +
                "  },\n" +
                "  \"addresses\" : [ {\n" +
                "    \"addressNumber\" : \"1\",\n" +
                "    \"zipCode\" : \"12345\",\n" +
                "    \"coHint\" : \"c/o Mustermann\",\n" +
                "    \"city\" : \"Sommerdorf\",\n" +
                "    \"street\" : \"Winterstrasse\"\n" +
                "  } ],\n" +
                "  \"_id\" : \"54eed97c58e770b72068ebf7\",\n" +
                "  \"id\" : \"08-cust-15\",\n" +
                "  \"taxnumber\" : \"47-tax-11\",\n" +
                "  \"contacts\" : [ ]\n" +
                "}";

        return json;
    }
}
