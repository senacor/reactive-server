package com.senacor.reactile.user;

import com.senacor.reactile.TestServices;
import com.senacor.reactile.VertxRule;
import org.junit.ClassRule;
import org.junit.Test;

import static com.senacor.reactile.domain.IdentityMatcher.hasId;
import static org.junit.Assert.assertThat;

public class UserServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(TestServices.UserService);
    private final UserService service = new UserServiceImpl(vertxRule.vertx());

    @Test
    public void thatUserCanBeLoggedIn() {
        User user = service.login(new UserId("momann")).toBlocking().first();
        assertThat(user, hasId("momann"));
    }

    @Test
    public void thatUserCanBeRead() {
        User user = service.getUser(new UserId("momann")).toBlocking().first();
        assertThat(user, hasId("momann"));
    }
}