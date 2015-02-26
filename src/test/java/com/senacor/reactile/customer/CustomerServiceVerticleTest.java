package com.senacor.reactile.customer;

import com.senacor.reactile.EventBusRule;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.ApplicationStartup;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CustomerServiceVerticleTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(ApplicationStartup.class, CustomerServiceVerticle.class);

    @Rule
    public final EventBusRule eventBusRule = new EventBusRule(vertxRule.vertx());

    @Test
    public void thatVerticleRespondsToMessage() throws InterruptedException, ExecutionException, TimeoutException {
        CustomerId customerId = new CustomerId("08-cust-15");
        Message<Customer> customer = eventBusRule.sendObservable(CustomerServiceVerticle.ADDRESS, customerId);
        assertThat(customer.body().getId(), is(equalTo(customerId)));
    }

}