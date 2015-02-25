package com.senacor.reactile.auth;

import com.senacor.reactile.VertxRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserDatabaseConnectorTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(UserDatabaseConnector.class);

    @Test(timeout = 300)
    public void thatUserCanBeObtainedFromDatabase() throws ExecutionException, InterruptedException {
        CompletableFuture<User> userFuture = new CompletableFuture<>();
        vertxRule.eventBus().sendObservable(UserDatabaseConnector.ADDRESS, new UserId("momann")).subscribe(
                message -> userFuture.complete((User) message.body()),
                userFuture::completeExceptionally
        );

        while (!userFuture.isDone()) {
            TimeUnit.MILLISECONDS.sleep(20);
        }

        User user = userFuture.get();
        assertThat(user, is(notNullValue()));
        assertThat(user.getId().getId(), is(equalTo("momann")));
        assertThat(user.getFirstName(), is(equalTo("Michael")));
        assertThat(user.getLastName(), is(equalTo("Omann")));

    }

}