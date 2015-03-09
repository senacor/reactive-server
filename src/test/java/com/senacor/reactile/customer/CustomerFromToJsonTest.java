package com.senacor.reactile.customer;

import com.google.common.io.Resources;
import io.vertx.core.json.JsonObject;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by rwinzing on 25.02.15.
 */
public class CustomerFromToJsonTest {

    @Test
    @Ignore
    public void thatJsonCanBeParsed() throws IOException {
        String json = getSampleCustomerJson();

        Customer customer = Customer.fromJson(new JsonObject(json));
        Customer refCustomer = getSampleCustomer();

        assertThat(customer, is(equalTo(refCustomer)));
    }

    private static Customer getSampleCustomer() {
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

    private String getSampleCustomerJson() throws IOException {
        return convertStreamToString(Resources.getResource("customer.json").openStream());
    }

    private static String convertStreamToString(InputStream is) {
        try(Scanner s = new java.util.Scanner(is).useDelimiter("\\A")) {
            return s.next();
        }
    }
}
