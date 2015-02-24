package com.senacor.reactile.customer;

import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Context;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import rx.functions.Action1;

public class CustomerServiceVerticle extends AbstractVerticle {


    public static final String ADDRESS = "customer";

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        printConfig();

        MessageConsumer<CustomerId> consumer = eventBus.consumer(ADDRESS);

//        consumer.toObservable()
//                .map(message -> message.body())
//                .subscribe(message -> eventBus.send("consumer-repository", "Hallo Ralph"));


        consumer.toObservable()
                .subscribe(message -> {
                    CustomerId customerId = message.body();
                    System.out.println("Receiving message: " + customerId);
                    System.out.println("Replying to " + message.replyAddress());
                    message.reply(customerId.getId());
                });

        registerCompletionHandler(consumer);
    }

    private void printConfig() {
        Context context = vertx.getOrCreateContext();
        System.out.println("Config for verticle " + this + " :" + context.config());
    }

    private void registerCompletionHandler(MessageConsumer<CustomerId> consumer) {
        Action1<Void> onNext = Void -> System.out.println("The handler registration has reached all nodes");
        Action1<Throwable> onError = Void -> System.out.println("Registration failed!");
        consumer.completionHandlerObservable().subscribe(onNext, onError);
    }

    @Override
    public void stop() throws Exception {

    }
}
