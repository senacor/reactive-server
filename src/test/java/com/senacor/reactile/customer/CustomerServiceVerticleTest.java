package com.senacor.reactile.customer;

import com.senacor.reactile.VertxRule;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CustomerServiceVerticleTest {

    @Rule
    public final VertxRule rule = new VertxRule(CustomerServiceVerticle.class);

    @Test(timeout = 500)
    public void thatVerticleRespondsToMessage() throws InterruptedException, ExecutionException {

        Vertx vertx = rule.vertx();
        EventBus eventBus = vertx.eventBus();

        CompletableFuture<Customer> responseFuture = new CompletableFuture<>();
        CustomerId id = new CustomerId("007");
        eventBus.sendObservable(CustomerServiceVerticle.ADDRESS, id)
                .map(response -> (Customer) response.body())
                .subscribe(responseFuture::complete);

        while (!responseFuture.isDone()) {
            TimeUnit.MILLISECONDS.sleep(20);
        }

        Customer customer = responseFuture.get();
        assertThat(customer.getId(), is(equalTo(id)));
    }

}