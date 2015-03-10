package com.senacor.reactile.user;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserServiceVerticleTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(Services.UserConnector, Services.UserService);

    @Test
    public void thatUserCanBeObtainedFromDatabase() throws ExecutionException, InterruptedException, TimeoutException {
        Message<User> userMessage = vertxRule.sendObservable(UserServiceVerticle.ADDRESS, new UserId("momann"), "get");
        User user = userMessage.body();
        assertIsUser(user);
    }

    @Test
    public void thatUserCanBeLoggedIn() throws ExecutionException, InterruptedException, TimeoutException {
        Message<User> userMessage = vertxRule.sendObservable(UserServiceVerticle.ADDRESS, new UserId("momann"), "login");
        User user = userMessage.body();
        assertIsUser(user);
    }

    @Test
    public void thatUserCanBeCreated() throws ExecutionException, InterruptedException, TimeoutException {
        User message = new User(new UserId("momann"), "Michael", "Omann");
        Message<User> userMessage = vertxRule.sendObservable(UserServiceVerticle.ADDRESS, message, "create");
        User user = userMessage.body();
        assertIsUser(user);
    }

    private void assertIsUser(User user) {
        assertThat(user, is(notNullValue()));
        assertThat(user.getId().getId(), is(equalTo("momann")));
        assertThat(user.getFirstName(), is(equalTo("Michael")));
        assertThat(user.getLastName(), is(equalTo("Omann")));
    }

}