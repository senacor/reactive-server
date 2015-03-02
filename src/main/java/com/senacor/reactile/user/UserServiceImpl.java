package com.senacor.reactile.user;

import io.vertx.core.eventbus.DeliveryOptions;
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
                .<User>sendObservable(UserServiceVerticle.ADDRESS, userId, action("get"))
                .map(Message::body);
    }

    private static DeliveryOptions action(String action) {
        return new DeliveryOptions().addHeader("action", action);
    }
}
