package com.senacor.reactile.bootstrap;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerId;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import java.math.BigDecimal;

import static de.flapdoodle.embed.process.collections.Collections.newArrayList;

public class MongoBootstrap extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        launchEmbeddedMongoObservable()
                .flatMap(res -> launchMongoServiceObservable())
                .flatMap(res -> writeSomethingObservable())
                .subscribe(
                        outcome -> System.out.println("Value " + outcome + " processed."),
                        startFuture::fail,
                        () -> {
                            startFuture.complete();
                            System.out.println("Mongo started and initialized.");
                        }

                );
    }

    private Observable<String> launchEmbeddedMongoObservable() {
        return vertx.deployVerticleObservable("service:io.vertx:vertx-mongo-embedded-db");
    }

    private Observable<String> launchMongoServiceObservable() {
        JsonObject config = new JsonObject()
                .put("connection_string", "mongodb://localhost:27018")
                .put("db_name", "reactile");
        ObservableFuture<String> observableHandler = RxHelper.observableFuture();
        getVertx().deployVerticle("service:io.vertx:mongo-service", new DeploymentOptions().setConfig(config), observableHandler.toHandler());
        return observableHandler;
    }

    private Observable<String> writeSomethingObservable() {
        MongoService service = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");

        Customer customer = newCustomer();


        ObservableFuture<String> custObservable = RxHelper.observableFuture();
        service.insert("customers", customer.toJson(), custObservable.toHandler());

        Account account_1 = newAccount1();

        ObservableFuture<String> acc1observable = RxHelper.observableFuture();
        service.insert("accounts", account_1.toJson(), acc1observable.toHandler());

        Account account_2 = newAccount2();
        ObservableFuture<String> acc2observable = RxHelper.observableFuture();
        service.insert("accounts", account_2.toJson(), acc2observable.toHandler());

        return custObservable.concatWith(acc1observable).concatWith(acc2observable);
    }

    private Account newAccount2() {
        return Account.anAccount()
                .withId("08-cust-15-ac-2")
                .withCustomerId(new CustomerId("08-cust-15"))
                .withBalance(new BigDecimal("20773"))
                .withCurrency("EUR")
                .build();
    }

    private Account newAccount1() {
        return Account.anAccount()
                .withId("08-cust-15-ac-1")
                .withCustomerId(new CustomerId("08-cust-15"))
                .withBalance(new BigDecimal("18773"))
                .withCurrency("EUR")
                .build();
    }

    private Customer newCustomer() {
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
