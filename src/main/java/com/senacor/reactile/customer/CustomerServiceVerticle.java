package com.senacor.reactile.customer;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Context;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;

public class CustomerServiceVerticle extends AbstractVerticle {


    public static final String ADDRESS = "customer";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        printConfig();

        MessageConsumer<CustomerId> consumer = eventBus.consumer(ADDRESS);

        consumer.toObservable()
                .subscribe(message -> {
                    CustomerId customerId = message.body();
                    log.info("Receiving message: " + customerId);
                    log.info("Replying to " + message.replyAddress());
                    message.reply(customerId.getId());
                });

        registerCompletionHandler(consumer);
    }

    private void printConfig() {
        Context context = vertx.getOrCreateContext();
        log.info("Config for verticle " + this + " :" + context.config());
    }

    private void registerCompletionHandler(MessageConsumer<CustomerId> consumer) {
        consumer.completionHandlerObservable().subscribe(
                Void -> log.info("The handler registration has reached all nodes"),
                Void -> log.error("Registration failed!")
        );
    }

    @Override
    public void stop() throws Exception {

    }
}
