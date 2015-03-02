package com.senacor.reactile.bootstrap;

import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerId;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.functions.Action1;

import java.util.*;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartup extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Action1<Throwable> errorhandler = cause -> {
            cause.printStackTrace();
        };

        launchEmbeddedMongoObservable().subscribe(res1 -> launchMongoServiceObservable().subscribe(res2 -> writeSomethingObservable().subscribe(outcome -> {
            System.out.println("all verticles started and dummy-data written");
            startFuture.complete();
        }), errorhandler), errorhandler);
    }

    private ObservableFuture<String> launchEmbeddedMongoObservable() {
        ObservableFuture<String> observable = RxHelper.observableFuture();
        vertx.deployVerticle("service:io.vertx:vertx-mongo-embedded-db", observable.asHandler());
        return observable;
    }

    private ObservableFuture<String> launchMongoServiceObservable() {
        JsonObject config = new JsonObject();
        config.put("address", "vertx.mongo").put("port", 27018);

        ObservableFuture<String> observable = RxHelper.observableFuture();
        vertx.deployVerticle("io.vertx.ext.mongo.MongoServiceVerticle", new DeploymentOptions().setConfig(config), observable.asHandler());
        return observable;
    }

    private ObservableFuture<String> writeSomethingObservable() {
        ObservableFuture<String> observable = RxHelper.observableFuture();

        MongoService service = MongoService.createEventBusProxy(vertx, "vertx.mongo");

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


        JsonObject doc = customer.toJson();
        service.insert("customers", doc, observable.asHandler());

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
