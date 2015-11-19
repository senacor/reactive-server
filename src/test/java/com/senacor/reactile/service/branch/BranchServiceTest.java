package com.senacor.reactile.service.branch;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerFixtures;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.customer.CustomerService;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static org.junit.Assert.assertThat;

public class BranchServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.BranchService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private BranchService service;

    private MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "branches");

    @Test
    public void thatBranchIsReturned() {
    }
}
