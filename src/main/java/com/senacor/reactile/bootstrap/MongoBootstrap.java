package com.senacor.reactile.bootstrap;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerId;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;
import rx.functions.Action1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MongoBootstrap extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Action1<Throwable> errorhandler = Throwable::printStackTrace;

        launchEmbeddedMongoObservable()
                .flatMap(res1 -> launchMongoServiceObservable())
                .subscribe(res2 -> writeSomethingObservable().subscribe(outcome -> {
                    System.out.println("Mongo started and initialized");
                    startFuture.complete();
                }), errorhandler);
    }

    private Observable<String> launchEmbeddedMongoObservable() {
        return vertx.deployVerticleObservable("service:io.vertx:vertx-mongo-embedded-db");
    }

    private Observable<String> launchMongoServiceObservable() {
        JsonObject config = new JsonObject()
                .put("connection_string", "mongodb://localhost:27018")
                .put("db_name", "reactile");
        return vertx.deployVerticleObservable("service:io.vertx:mongo-service", new DeploymentOptions().setConfig(config));
    }

    private Observable<String> writeSomethingObservable() {
        MongoService service = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");
        Observable<String> observable = null;

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
                .withFirstname("Hans")
                .withLastname("Dampf")
                .withAddresses(addresses)
                .withTaxCountry(new Country("England", "EN"))
                .withTaxNumber("47-tax-11").build();


        ObservableFuture<String> custObservable = RxHelper.observableFuture();
        JsonObject doc = customer.toJson();
        service.insert("customers", doc, custObservable.asHandler());
        observable = custObservable;

        Account account_1 = Account.anAccount()
                .withId("08-cust-15-ac-1")
                .withCustomerId(new CustomerId("08-cust-15"))
                .withBalance(new BigDecimal("18773"))
                .withCurrency("EUR")
                .build();
        ObservableFuture<String> acc1observable = RxHelper.observableFuture();
        service.insert("accounts", account_1.toJson(), acc1observable.asHandler());
        observable = observable.mergeWith(acc1observable);

        Account account_2 = Account.anAccount()
                .withId("08-cust-15-ac-2")
                .withCustomerId(new CustomerId("08-cust-15"))
                .withBalance(new BigDecimal("20773"))
                .withCurrency("EUR")
                .build();
        ObservableFuture<String> acc2observable = RxHelper.observableFuture();
        service.insert("accounts", account_2.toJson(), acc2observable.asHandler());
        observable = observable.mergeWith(acc2observable);

        return observable;
    }


    private void findSomething() {
        JsonObject query = new JsonObject().put("id", "007");

        /*
        MongoService service = MongoService.createEventBusProxy(vertx, "vertx.mongo");


        service.find("customers", query, outcome -> {
            if (outcome.succeeded()) {
                System.out.println("found something");
            } else {
                System.out.println("failed: "+outcome.cause());
                outcome.cause().printStackTrace();
            }
        });
        */


        JsonObject find = new JsonObject().put("collection", "customers")
                .put("query", query);

        vertx.eventBus().send("vertx.mongo", find,
                new DeliveryOptions().addHeader("action", "find"), outcome -> {

                    if (outcome.succeeded()) {
                        System.out.println(" ==> " + outcome.result().body());
                    } else {
                        outcome.cause().printStackTrace();
                    }
                });

    }
}
