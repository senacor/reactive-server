package com.senacor.reactile;

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
        return sendObservable(address, message, DEFAULT_TIMEOUT);
    }

    public <T> Message<T> sendObservable(String address, Object message, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Message<T>> responseFuture = new CompletableFuture<>();
        vertx.eventBus().<T>sendObservable(address, message).subscribe(responseFuture::complete);
        return responseFuture.get(timeout, TimeUnit.MILLISECONDS);

    }
}
