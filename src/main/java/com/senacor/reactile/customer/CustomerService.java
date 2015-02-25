package com.senacor.reactile.customer;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Context;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;

public class CustomerService extends AbstractVerticle {


    public static final String ADDRESS = "customer";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        printConfig();

        MessageConsumer<CustomerId> consumer = eventBus.consumer(ADDRESS);
        consumer.toObservable().subscribe(message ->  message.reply(getCustomer(message.body().getId())));

    }

    private Customer getCustomer(String id) {
        return Customer.newBuilder()
                .withId(id)
                .withTaxCountry(new Country("Deutschland", "DE"))
                .withTaxNumber("SSDS3242342342342")
                .build();
    }

    private void printConfig() {
        Context context = vertx.getOrCreateContext();
        log.info("Config for verticle " + this + " :" + context.config());
    }

    @Override
    public void stop() throws Exception {

    }
}
