package com.senacor.reactile.service.user;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

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

    @Test
    public void thatUserCanBeLoggedIn() {
        User user = service.loginObservable(new UserId("momann")).toBlocking().first();
        assertIsUser(user);
    }

    @Test
    public void thatUserCanBeRead() {
        User user = service.getUserObservable(new UserId("momann")).toBlocking().first();
        assertIsUser(user);
    }

    private static void assertIsUser(User user) {
        assertThat(user, is(notNullValue()));
        assertThat(user.getId().getId(), is(equalTo("momann")));
        assertThat(user.getFirstName(), is(equalTo("Michael")));
        assertThat(user.getLastName(), is(equalTo("Omann")));
    }
}