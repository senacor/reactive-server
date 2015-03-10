package com.senacor.reactile.customer;

import com.google.common.base.Stopwatch;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.MongoBootstrap;
import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.senacor.reactile.header.Headers.action;
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
        Message<Customer> customer = vertxRule.sendBlocking(CustomerServiceVerticle.ADDRESS, customerId, "getCustomer");
        assertThat(customer.body().getId(), is(equalTo(customerId)));
    }

    @Test
    public void thatCustomerCanBeWritten() throws InterruptedException, ExecutionException, TimeoutException {
        CustomerId customerId = new CustomerId("08-cust-15");
        Message<Customer> customer = vertxRule.sendBlocking(CustomerServiceVerticle.ADDRESS, customerId, "add");
        assertThat(customer.body().getId(), is(equalTo(customerId)));
    }

    @Test
    public void thatCustomerCanBeUpdated() throws InterruptedException, ExecutionException, TimeoutException {
        CustomerId customerId = new CustomerId("08-cust-15");
        Message<Customer> customer = vertxRule.sendBlocking(CustomerServiceVerticle.ADDRESS, customerId, "getCustomer");
        assertThat(customer.body().getId(), is(equalTo(customerId)));
    }

    @Test(timeout = 5000)
    public void writeManyCustomers() throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<Customer> written = new ArrayList<>();

        CustomerFixtures.randomCustomers(500)
                .flatMap(customer -> vertxRule.eventBus().<Customer>sendObservable(CustomerServiceVerticle.ADDRESS, customer, action("add")))
                .map(Message::body)
                .subscribe(
                        written::add,
                        throwable -> System.out.println("Failed due to " + throwable.getMessage()),
                        () -> System.out.println("Finished in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " Milliseconds")
                );
        while(written.size() < 500) {
            Thread.sleep(40);
        }
    }


}