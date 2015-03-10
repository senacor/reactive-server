package com.senacor.reactile.customer;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.MongoBootstrap;
import com.senacor.reactile.mongo.ObservableMongoService;
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
    public final VertxRule vertxRule = new VertxRule();
    {
        vertxRule.deployVerticle(Services.EmbeddedMongo, Services.CustomerService);
        vertxRule.deployVerticle(MongoBootstrap.class);
    }

    private final ObservableMongoService mongoService = ObservableMongoService.from(vertxRule.vertx());

    private final CustomerMongoInitializer initializer = new CustomerMongoInitializer(mongoService);

    @Test
    public void thatVerticleRespondsToMessage() throws InterruptedException, ExecutionException, TimeoutException {
        initializer.writeBlocking(CustomerFixtures.defaultCustomer());
        CustomerId customerId = new CustomerId("08-cust-15");
        Message<Customer> customer = vertxRule.sendObservable(CustomerServiceVerticle.ADDRESS, customerId, "getCustomer");
        assertThat(customer.body().getId(), is(equalTo(customerId)));
    }

    @Test(timeout = 5000)
    public void fillWithTestdata() {
        CustomerMongoInitializer initializer = new CustomerMongoInitializer(mongoService);
        initializer.writeBlocking(500);

    }


}