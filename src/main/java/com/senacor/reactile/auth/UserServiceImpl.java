package com.senacor.reactile.auth;

import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class UserServiceImpl implements UserService {
    private final Vertx vertx;

    public UserServiceImpl(Vertx vertx) {

        this.vertx = vertx;
    }

    @Override
    public Observable<User> getUser(UserId userId) {
        return vertx.eventBus()
                .<User>sendObservable(UserServiceVerticle.ADDRESS, userId)
                .map(Message::body);
    }
}
