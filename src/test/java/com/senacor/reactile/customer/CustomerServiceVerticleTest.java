package com.senacor.reactile.customer;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.ClassRule;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CustomerServiceVerticleTest {

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.CustomerService);

    public static final String COLLECTION = "customers";

    private final ObservableMongoService mongoService = ObservableMongoService.from(vertxRule.vertx());

    private final MongoInitializer initializer = new MongoInitializer(mongoService, COLLECTION);

    @Test
    public void thatCustomerCanBeRead() throws Exception {
        Customer customer = CustomerFixtures.defaultCustomer();
        initializer.writeBlocking(customer);
        CustomerId customerId = customer.getId();
        Message<Customer> returnedCustomer = vertxRule.sendBlocking(CustomerServiceVerticle.ADDRESS, customerId, "getCustomer");
        assertThat(returnedCustomer.body().getId(), is(equalTo(customerId)));
    }

    @Test
    public void thatCustomerCanBeWritten() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();
        vertxRule.sendBlocking(CustomerServiceVerticle.ADDRESS, customer, "add");
        Customer fromMongo = mongoService.findOne(COLLECTION, new JsonObject().put("id", customer.getId().toValue()))
                .map(Customer::fromJson)
                .toBlocking()
                .first();

        assertThat(fromMongo.getId(), is(equalTo(customer.getId())));
    }

}