package com.senacor.reactile.customer;

import com.google.common.base.Stopwatch;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.MongoBootstrap;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.ClassRule;
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

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule();

    static {
        vertxRule.deployVerticle(Services.EmbeddedMongo, Services.CustomerService);
        vertxRule.deployVerticle(MongoBootstrap.class);
    }

    public static final String COLLECTION = "customers";

    private final ObservableMongoService mongoService = ObservableMongoService.from(vertxRule.vertx());

    private final MongoInitializer initializer = new MongoInitializer(mongoService, COLLECTION);

    @Test
    public void thatVerticleRespondsToMessage() throws InterruptedException, ExecutionException, TimeoutException {
        Customer customer = CustomerFixtures.defaultCustomer();
        initializer.writeBlocking(customer);
        CustomerId customerId = customer.getId();
        Message<Customer> returnedCustomer = vertxRule.sendBlocking(CustomerServiceVerticle.ADDRESS, customerId, "getCustomer");
        assertThat(returnedCustomer.body().getId(), is(equalTo(customerId)));
    }

    @Test
    public void thatCustomerCanBeWritten() throws InterruptedException, ExecutionException, TimeoutException {
        Customer customer = CustomerFixtures.randomCustomer();
        vertxRule.sendBlocking(CustomerServiceVerticle.ADDRESS, customer, "add");
        Customer fromMongo = mongoService.findOne(COLLECTION, new JsonObject().put("id", customer.getId().toValue()))
                .map(Customer::fromJson)
                .toBlocking()
                .first();

        assertThat(fromMongo.getId(), is(equalTo(customer.getId())));
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