package com.senacor.reactile.user;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static com.senacor.reactile.header.Headers.action;

public class UserConnector {

    private final Vertx vertx;
    private final long latency = 35;

    public UserConnector(Vertx vertx) {
        this.vertx = vertx;
    }

    public Observable<User> findUser(UserId userId) {
        return send(userId, action("findOne"));
    }

    public Observable<User> authenticate(UserId userId) {
        return send(userId, action("login"));
    }

    public Observable<User> addUser(User user) {
        return send(user, action("add"));
    }

    private Observable<User> send(Object payload, DeliveryOptions action) {
        return getEventBus().<User>sendObservable(UserConnectorVerticle.ADDRESS, payload, action)
                .delay(latency, TimeUnit.MILLISECONDS)
                .map(Message::body);
    }

    private EventBus getEventBus() {
        return vertx.eventBus();
    }
}