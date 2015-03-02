package com.senacor.reactile.customer;

import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func5;

import java.util.Arrays;
import java.util.List;

public class CustomerMongoInitializer {

    private final io.vertx.rxjava.core.Vertx vertx;

    public CustomerMongoInitializer(io.vertx.rxjava.core.Vertx vertx) {
        this.vertx = vertx;
    }

    public void write(int count) {
        MongoService service = MongoService.createEventBusProxy((Vertx) vertx.getDelegate(), "vertx.mongo");

        Observable<Customer> testCustomers = Observable.zip(
                addressNumber(count),
                firstName(),
                lastName(),
                streetName(),
                streetType(), zipToCustomer()
        );

        testCustomers.flatMap(insert(service)).subscribe(
                outcome -> System.out.println("outcome = " + outcome),
                Throwable::printStackTrace,
                () -> System.out.println("done!!!!"));
    }

    private Func1<Customer, Observable<? extends String>> insert(MongoService service) {
        return customer -> {
            ObservableFuture<String> newOne = RxHelper.observableFuture();
            service.insertWithOptions("customers", customer.toJson(), WriteOption.UNACKNOWLEDGED, newOne.asHandler());
            return newOne;
        };
    }

    private Func5<Integer, String, String, String, String, Customer> zipToCustomer() {
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


    private Observable<Integer> addressNumber(int count) {
        return Observable.range(1000, count);
    }

    private Observable<String> streetName() {
        List streets = Arrays.asList("Winter", "Frühling", "Sommer", "Herbst", "Amsel", "Drossel", "Fink", "Star");
        return Observable.from(streets).repeat();
    }

    private Observable<String> streetType() {
        List streets = Arrays.asList("strasse", "weg", "pfad");
        return Observable.from(streets).repeat();
    }

    private Observable<String> firstName() {
        List streets = Arrays.asList("Adam", "Anneliese", "Berthold", "Berta", "Christopher", "Charlotte", "Dennis", "Dorothea");
        return Observable.from(streets).repeat();
    }

    private Observable<String> lastName() {
        List streets = Arrays.asList("Kugler", "Lurchig", "Monheim", "Naaber", "Peine", "Quaid", "Rastatt");
        return Observable.from(streets).repeat();
    }
}
