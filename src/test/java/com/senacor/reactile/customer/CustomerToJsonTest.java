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
public class CustomerToJsonTest {
    @Test
    public void thatValidJsonIsGenerated() {
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


        JsonObject ref = new JsonObject("{\"id\":\"08-cust-15\",\"addresses\":[{\"coHint\":\"c/o Mustermann\",\"street\":\"Winterstrasse\",\"zipCode\":\"12345\",\"addressNumber\":\"1\",\"city\":\"Sommerdorf\",\"country\":{\"name\":\"Deutschland\",\"code\":\"DE\"}}],\"contacts\":[],\"taxCountry\":{\"name\":\"England\",\"code\":\"EN\"},\"taxnumber\":\"47-tax-11\"}");
        assertThat(customer.toJson().toString(), is(equalTo(ref.toString())));
    }
}
