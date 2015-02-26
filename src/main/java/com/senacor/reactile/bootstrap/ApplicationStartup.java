package com.senacor.reactile.bootstrap;

import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerId;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartup extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        launchEmbeddedMongo();
    }

    private void launchEmbeddedMongo() {
        vertx.deployVerticle("service:io.vertx:vertx-mongo-embedded-db", response -> {
            if (response.succeeded()) {
                System.out.println("embedded-mongo-service started: " + response.result());
                launchMongoService();
            } else {
                System.out.println("embedded-mongo-service failed: " + response.cause());
                throw new RuntimeException("embedded-mongo-service failed: " + response.cause());
            }
        });

    }

    private void launchMongoService() {
        JsonObject config = new JsonObject();
        config.put("address", "vertx.mongo").put("port", 27018);
        // config.put("username", "john").put("password", "passw0rd");
        // service:io.vertx:ext-mongo
        vertx.deployVerticle("io.vertx.ext.mongo.MongoServiceVerticle", new DeploymentOptions().setConfig(config), response -> {
            if (response.succeeded()) {
                System.out.println("mongo-service started: " + response.result());
                writeSomething();
                findSomething();
            } else {
                System.out.println("mongo-service failed: " + response.cause());
                throw new RuntimeException("mongo-service failed: " + response.cause());
            }
        });
    }

    private void writeSomething() {
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
                .withAddresses(addresses)
                .withTaxCountry(new Country("England", "EN"))
                .withTaxNumber("47-tax-11").build();


        JsonObject doc = customer.toJson();

        service.insert("customers", doc, outcome -> {
            if (outcome.succeeded()) {
                System.out.println("yay: "+outcome.result());
            } else {
                System.out.println("boo: "+outcome.cause());
            }
        });
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
