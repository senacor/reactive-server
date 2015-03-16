package com.senacor.reactile.customer;

import com.senacor.reactile.TestServices;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.mongo.MongoInitializer;
import org.junit.ClassRule;
import org.junit.Test;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static org.junit.Assert.assertThat;

public class CustomerServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(TestServices.CustomerService);
    private final CustomerService service = new CustomerServiceImpl(vertxRule.vertx());

    private final MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "customers");

    @Test
    public void thatCustomerIsReturned() {
        mongoInitializer.writeBlocking(CustomerFixtures.newCustomer("cust-asdfghjk"));
        Customer customer = service.getCustomer(new CustomerId("cust-asdfghjk")).toBlocking().first();
        assertThat(customer, hasId("cust-asdfghjk"));
    }


}