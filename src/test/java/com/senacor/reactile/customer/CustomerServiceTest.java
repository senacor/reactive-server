package com.senacor.reactile.customer;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import io.vertx.core.json.JsonObject;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static com.senacor.reactile.customer.CustomerFixtures.randomCustomer;
import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CustomerServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.CustomerService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.customer.CustomerService service;

    private MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "customers");

    @Test
    public void thatCustomerIsReturned() {
        mongoInitializer.writeBlocking(CustomerFixtures.newCustomer("cust-asdfghjk"));
        Customer customer = service.getCustomerObservable(new CustomerId("cust-asdfghjk")).toBlocking().first();
        assertThat(customer, hasId("cust-asdfghjk"));
    }

    @Test
    public void thatCustomerCanBeCreated() {
        Customer customer = service.createCustomerObservable(randomCustomer("cust-254")).toBlocking().first();
        assertThat(customer, hasId("cust-254"));
    }

    @Test
    public void thatCustomerCanBeUpdated() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();
        mongoInitializer.writeBlocking(customer);

        Address newAddress = new Address("","Teststreet","TestPLZ","8", "Testcity", new Country("Deutschland", "DE"), 1);
        service.updateAddressObservable(customer.getId(),newAddress).toBlocking().first();

        Customer customerUpdated = service.getCustomerObservable(customer.getId()).toBlocking().first();

        assertThat(customerUpdated.getAddresses().get(0).getCity(), is(equalTo(newAddress.getCity())));
    }


}