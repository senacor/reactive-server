package com.senacor.reactile.customer;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static com.senacor.reactile.customer.CustomerFixtures.randomCustomer;
import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static org.junit.Assert.assertThat;

public class CustomerServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.CustomerService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private CustomerService service;

    private MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "customers");

    @Test
    public void thatCustomerIsReturned() {
        mongoInitializer.writeBlocking(CustomerFixtures.newCustomer("cust-asdfghjk"));
        Customer customer = service.getCustomer(new CustomerId("cust-asdfghjk")).toBlocking().first();
        assertThat(customer, hasId("cust-asdfghjk"));
    }

    @Test
    public void thatCustomerCanBeCreated() {
        Customer customer = service.createCustomer(randomCustomer("cust-254")).toBlocking().first();
        assertThat(customer, hasId("cust-254"));
    }


}