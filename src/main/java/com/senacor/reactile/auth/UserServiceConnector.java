package com.senacor.reactile.auth;

import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class UserServiceConnector {
    private final long latency = 35;

    private final UserDatabase database;
    private final Vertx vertx;

    public UserServiceConnector(Vertx vertx) {
        this.vertx = vertx;
        this.database = new SimpleUserDatabase();
    }

    public Observable<User> findUser(UserId userId) {
        return Observable.just(database.findUser(userId)).delay(latency, TimeUnit.MILLISECONDS);
    }

    public Observable<User> authenticate(UserId userId) {
        return Observable.just(database.login(userId)).delay(latency, TimeUnit.MILLISECONDS);
    }

    public Observable<User> addUser(User user) {
        database.addUser(user);
        return Observable.just(user).delay(latency, TimeUnit.MILLISECONDS);
    }
}