package com.senacor.reactile.customer;

import com.senacor.reactile.VertxRule;
import com.senacor.reactile.codec.DomainObjectMessageCodec;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class CustomerServiceVerticleTest {

    @Rule
    public final VertxRule rule = new VertxRule(CustomerServiceVerticle.class);

    @Test(timeout = 500)
    public void thatVerticleRespondsToMessage() throws InterruptedException {

        Vertx vertx = rule.vertx();
        EventBus eventBus = vertx.eventBus();

        registerDomainObjectCodec(vertx);

        CompletableFuture<Object> responseFuture = new CompletableFuture<>();
        eventBus.sendObservable(CustomerServiceVerticle.ADDRESS, new CustomerId("007")).subscribe(response -> responseFuture.complete(response.body()));

        while (!responseFuture.isDone()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

    }

    private void registerDomainObjectCodec(Vertx vertx) {
        Stream.of(Address.class, Contact.class, Country.class, Customer.class, CustomerId.class).forEach(clazz -> {
            ((io.vertx.core.eventbus.EventBus)vertx.eventBus().getDelegate()).registerDefaultCodec(clazz, DomainObjectMessageCodec.from(clazz));
        });
    }

}