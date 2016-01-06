package com.senacor.reactile.service.user;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import io.vertx.core.json.JsonObject;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import javax.inject.Inject;
import java.util.ArrayList;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class UserServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.UserService);
    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private UserService service;

    private MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "users");


    @Test
    public void thatUserIsReturned() {
        mongoInitializer.writeBlocking(new User(new UserId("cust-asdfghjk"), "Walter", "Sion","2"));
        User user = service.getUser(new UserId("cust-asdfghjk")).toBlocking().first();
        assertThat(user, hasId("cust-asdfghjk"));
    }

    @Test
    public void thatUserCanBeCreated() {
        User user = service.createUser(new User(new UserId("cust-254"), "Walter", "Sion","2")).toBlocking().first();
        assertThat(user, hasId("cust-254"));
    }

    @Test
    public void thatUserCanBeLoggedIn() {
        mongoInitializer.writeBlocking(new User(new UserId("momann"),"Michael", "Omann","1"));
        User user = service.login(new UserId("momann")).toBlocking().first();
        assertIsUser(user);
    }

    @Test
    public void thatUserCanBeFoundByLastname() {
        User jumbo = new User(new UserId("Find"), "Jumbo", "Elefant", "1");
        mongoInitializer.writeBlocking(jumbo);
        ArrayList<User> collect = service.findUser(new JsonObject().put("lastName", "Elefant"))
                .flatMap(jsonizableList -> Observable.from(jsonizableList.toList()))
                .doOnNext(System.out::println)
                .map(jsonObject1 -> User.fromJson(jsonObject1))
                .collect(() -> new ArrayList<User>(), (o, jsonObject) -> o.add(jsonObject))
                .toBlocking().first();
        assertFalse(collect.isEmpty());
        assertEquals(1, collect.size());
    }

    @Test
    public void thatUsersCanBeFoundByBranch() {
        mongoInitializer.writeBlocking(new User(new UserId("1"), "User of Branch 1", "Find", "1"));
        mongoInitializer.writeBlocking(new User(new UserId("2"), "User of Branch 2", "Find", "2"));
        mongoInitializer.writeBlocking(new User(new UserId("3"), "User of Branch 1", "Find", "1"));

        ArrayList<User> collect = service.findUser(new JsonObject().put("branchId", "1"))
                .flatMap(jsonizableList -> Observable.from(jsonizableList.toList()))
                .doOnNext(System.out::println)
                .map(jsonObject1 -> User.fromJson(jsonObject1))
                .collect(() -> new ArrayList<User>(), (o, user) -> o.add(user))
                .toBlocking().first();
        assertFalse(collect.isEmpty());
        assertEquals(2, collect.size());
    }

    @Test
    public void thatAllUsersCanBeFound() {
        mongoInitializer.writeBlocking(new User(new UserId("Allomann"), "Michael", "Omann", "1"), new User(new UserId("swalter"), "Simon", "Walter", "2"));
        ArrayList<User> collect = service.findUser(new JsonObject())
                .flatMap(jsonizableList -> Observable.from((Iterable<JsonObject>)jsonizableList.toList()))
                .map(jsonObject1 -> User.fromJson(jsonObject1))
                .collect(() -> new ArrayList<User>(), (o, jsonObject) -> o.add(jsonObject))
                .toBlocking().first();
        assertFalse(collect.isEmpty());
        assertTrue(collect.size() > 2);
    }

    private static void assertIsUser(User user) {
        assertThat(user, is(notNullValue()));
        assertThat(user.getId().getId(), is(equalTo("momann")));
        assertThat(user.getFirstName(), is(equalTo("Michael")));
        assertThat(user.getLastName(), is(equalTo("Omann")));
        assertThat(user.getBranchId(), is(equalTo("1")));
    }
}