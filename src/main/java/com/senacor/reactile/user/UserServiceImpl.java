package com.senacor.reactile.user;

import com.senacor.reactile.hystrix.interception.HystrixCmd;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import javax.inject.Inject;

import static com.senacor.reactile.header.Headers.action;

public class UserServiceImpl implements UserService {
    private final Vertx vertx;

    @Inject
    public UserServiceImpl(Vertx vertx) {

        this.vertx = vertx;
    }

    @Override
    @HystrixCmd(UserServiceImplGetUserCommand.class)
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
