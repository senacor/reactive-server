package com.senacor.reactile.service.user;

import com.senacor.reactile.hystrix.interception.HystrixCmd;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

import javax.inject.Inject;

public class UserServiceImpl implements UserService {
    private final UserDatabase database;

    @Inject
    public UserServiceImpl(UserDatabase userDatabase) {
        this.database = userDatabase;
    }

    @Override
    public void getUser(UserId userId, Handler<AsyncResult<User>> resultHandler) {
        Rx.bridgeHandler(getUser(userId), resultHandler);
    }

    @Override
    public void login(UserId userId, Handler<AsyncResult<User>> resultHandler) {
        Rx.bridgeHandler(Observable.just(database.login(userId)), resultHandler);
    }

    @HystrixCmd(UserServiceImplGetUserCommand.class)
    private Observable<User> getUser(UserId userId) {
        return Observable.just(database.findUser(userId));
    }
}
