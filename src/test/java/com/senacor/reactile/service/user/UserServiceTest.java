package com.senacor.reactile.service.user;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerFixtures;
import com.senacor.reactile.service.customer.CustomerId;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static com.senacor.reactile.service.customer.CustomerFixtures.randomCustomer;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.UserService);
    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.service.user.UserService service;

    private MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "users");


    @Test
    public void thatUserIsReturned() {
        mongoInitializer.writeBlocking(new User(new UserId("cust-asdfghjk"), "Walter", "Sion","2"));
        User user = service.getUserObservable(new UserId("cust-asdfghjk")).toBlocking().first();
        assertThat(user, hasId("cust-asdfghjk"));
    }

    @Test
    public void thatUserCanBeCreated() {
        User user = service.createUserObservable(new User(new UserId("cust-254"), "Walter", "Sion","2")).toBlocking().first();
        assertThat(user, hasId("cust-254"));
    }

    @Test
    public void thatUserCanBeLoggedIn() {
        mongoInitializer.writeBlocking(new User(new UserId("momann"),"Michael", "Omann","1"));
        User user = service.loginObservable(new UserId("momann")).toBlocking().first();
        assertIsUser(user);
    }

    private static void assertIsUser(User user) {
        assertThat(user, is(notNullValue()));
        assertThat(user.getId().getId(), is(equalTo("momann")));
        assertThat(user.getFirstName(), is(equalTo("Michael")));
        assertThat(user.getLastName(), is(equalTo("Omann")));
        assertThat(user.getBranchId(), is(equalTo("1")));
    }
}