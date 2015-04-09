package com.senacor.reactile.customer;

import rx.Observable;
import rx.functions.Func5;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;

public final class CustomerFixtures {

    private static final Random rd = new Random();
    public static final List STREETS = Arrays.asList("Winter", "Frühling", "Sommer", "Herbst", "Amsel", "Drossel", "Fink", "Star");
    public static final List STREET_TYPES = Arrays.asList("strasse", "weg", "pfad");
    public static final List FIRST_NAMES = Arrays.asList("Adam", "Anneliese", "Berthold", "Berta", "Christopher", "Charlotte", "Dennis", "Dorothea");
    public static final List LAST_NAMES = Arrays.asList("Kugler", "Lurchig", "Monheim", "Naaber", "Peine", "Quaid", "Rastatt");
    public static final List CITIES = Arrays.asList("München", "Stuttgart", "Poppenhausen", "Bonn", "Berlin", "Hamburg", "Mainz", "Leibzig", "Hintertuxing");

    private CustomerFixtures() {
    }

    public static Customer defaultCustomer() {
        return newCustomer("08-cust-15");
    }

    public static Customer randomCustomer() {
        return randomCustomerBuilder().build();
    }
    public static Customer randomCustomer(String customerId) {
        return randomCustomer(new CustomerId(customerId));
    }

    public static Customer randomCustomer(CustomerId customerId) {
        return randomCustomerBuilder()
                .withId(customerId).build();
    }

    public static Customer newCustomer(String id) {
        return randomCustomerBuilder()
                .withId(new CustomerId(id))
                .build();
    }

    private static final Customer.Builder randomCustomerBuilder() {
        return Customer.newBuilder()
                .withId("cust-" + uuid())
                .withFirstname(pickRandom(FIRST_NAMES))
                .withLastname(pickRandom(LAST_NAMES))
                .withAddresses(newArrayList(randomAddressBuilder().build()))
                .withTaxCountry(rd.nextBoolean() ? new Country("England", "EN") : new Country("Deutschland", "DE"))
                .withTaxNumber("" + rd.nextInt(99) + "-tax-" + rd.nextInt(99));
    }

    public static Address.Builder randomAddressBuilder() {
        return Address.anAddress()
                .withAddressNumber("" + rd.nextInt(99))
                .withCoHint("c/o Ramstein")
                .withStreet(pickRandom(STREETS))
                .withCity(pickRandom(CITIES))
                .withZipCode("" + rd.nextInt(90000) + 10000)
                .withCountry(new Country("Deutschland", "DE"))
                .withIndex(1);
    }

    private static String pickRandom(List<String> source) {
        return source.get(rd.nextInt(source.size()));
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
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
                    .withCountry(new Country("Deutschland", "DE"))
                    .withIndex(1)
                    .build();

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
        return Observable.from(STREETS).repeat();
    }

    private static Observable<String> streetType() {
        return Observable.from(STREET_TYPES).repeat();
    }

    private static Observable<String> firstName() {
        return Observable.from(FIRST_NAMES).repeat();
    }

    private static Observable<String> lastName() {
        return Observable.from(LAST_NAMES).repeat();
    }

}
