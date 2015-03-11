package com.senacor.reactile.customer;

import rx.Observable;
import rx.functions.Func5;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static de.flapdoodle.embed.process.collections.Collections.newArrayList;

public final class CustomerFixtures {

    private static final Random rd = new Random();

    private CustomerFixtures() {
    }

    public static Customer defaultCustomer() {
        return newCustomer("08-cust-15");
    }

    public static Customer randomCustomer() {
        return newCustomer(UUID.randomUUID().toString());
    }

    public static Customer newCustomer(String id) {
        return Customer.newBuilder()
                .withId(new CustomerId(id))
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

    public static Observable<Customer> randomCustomers(int count) {
        return Observable
                .zip(addressNumber(count), firstName(), lastName(), streetName(), streetType(),
                        zipToCustomer());
    }

    private static Func5<Integer, String, String, String, String, Customer> zipToCustomer() {
        return (addrNum, fname, lname, sname, stype) -> {
            Address addr = Address.anAddress()
                    .withAddressNumber("addr-" + addrNum)
                    .withStreet(sname + stype)
                    .withCoHint("")
                    .withCity("Nürnberg")
                    .withZipCode("12345")
                    .withCountry(new Country("Deutschland", "DE")).build();

            return Customer.newBuilder()
                    .withId("cust-" + addrNum)
                    .withFirstname(fname)
                    .withLastname(lname)
                    .withAddresses(Arrays.asList(addr))
                    .withTaxNumber("tax-" + addrNum)
                    .withTaxCountry(new Country("Deutschland", "DE"))
                    .build();
        };
    }

    private static Observable<Integer> addressNumber(int count) {
        return Observable.range(1000, count);
    }

    private static Observable<String> streetName() {
        List streets = Arrays.asList("Winter", "Frühling", "Sommer", "Herbst", "Amsel", "Drossel", "Fink", "Star");
        return Observable.from(streets).repeat();
    }

    private static Observable<String> streetType() {
        List streets = Arrays.asList("strasse", "weg", "pfad");
        return Observable.from(streets).repeat();
    }

    private static Observable<String> firstName() {
        List streets = Arrays.asList("Adam", "Anneliese", "Berthold", "Berta", "Christopher", "Charlotte", "Dennis", "Dorothea");
        return Observable.from(streets).repeat();
    }

    private static Observable<String> lastName() {
        List streets = Arrays.asList("Kugler", "Lurchig", "Monheim", "Naaber", "Peine", "Quaid", "Rastatt");
        return Observable.from(streets).repeat();
    }

}
