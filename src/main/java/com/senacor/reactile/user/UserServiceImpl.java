package com.senacor.reactile.user;

import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import static com.senacor.reactile.header.Headers.action;

public class UserServiceImpl implements UserService {
    private final Vertx vertx;

    public UserServiceImpl(Vertx vertx) {

        this.vertx = vertx;
    }

    @Override
    public Observable<User> getUser(UserId userId) {
        return vertx.eventBus()
                .<User>sendObservable(UserServiceVerticle.ADDRESS, userId, action("get"))
                .map(Message::body);
    }

    @Override
    public Observable<User> login(UserId userId) {
        return vertx.eventBus()
                .<User>sendObservable(UserServiceVerticle.ADDRESS, userId, action("login"))
                .map(Message::body);
    }

}
