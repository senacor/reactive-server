package com.senacor.reactile.customer;

import static de.flapdoodle.embed.process.collections.Collections.newArrayList;

public final class CustomerFixtures {


    private CustomerFixtures() {
    }

    public static Customer defaultCustomer() {
        return Customer.newBuilder()
                .withId(new CustomerId("08-cust-15"))
                .withFirstname("Hans")
                .withLastname("Dampf")
                .withAddresses(newArrayList(Address.anAddress()
                        .withAddressNumber("1")
                        .withCoHint("c/o Mustermann")
                        .withStreet("Winterstrasse")
                        .withCity("Sommerdorf")
                        .withZipCode("12345")
                        .withCountry(new Country("Deutschland", "DE")).build()))
                .withTaxCountry(new Country("England", "EN"))
                .withTaxNumber("47-tax-11").build();
    }
}
