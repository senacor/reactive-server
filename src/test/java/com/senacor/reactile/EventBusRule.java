package com.senacor.reactile;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.rules.ExternalResource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EventBusRule extends ExternalResource {
    public static final int DEFAULT_TIMEOUT = 300;
    private final Vertx vertx;

    public EventBusRule(Vertx vertx) {
        this.vertx = vertx;
    }

    public <T> Message<T> sendObservable(String address, Object message) throws InterruptedException, ExecutionException, TimeoutException {
        return sendObservable(address, message, null);
    }

    public <T> Message<T> sendObservable(String address, Object message, String action) throws InterruptedException, ExecutionException, TimeoutException {
        return sendObservable(address, message, action, DEFAULT_TIMEOUT);
    }

    public <T> Message<T> sendObservable(String address, Object message, String action, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        DeliveryOptions options = new DeliveryOptions();
        if (action != null) options.addHeader("action", action);
        return sendObservable(address, message, options, timeout);

    }

    public <T> Message<T> sendObservable(String address, Object message, DeliveryOptions options, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Message<T>> responseFuture = new CompletableFuture<>();
        vertx.eventBus().<T>sendObservable(address, message, options).subscribe(responseFuture::complete);
        return responseFuture.get(timeout, TimeUnit.MILLISECONDS);

    }

}