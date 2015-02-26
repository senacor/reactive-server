package com.senacor.reactile.auth;

import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import rx.Observable;

public class UserServiceVerticle extends AbstractServiceVerticle {

    public static final String ADDRESS = "UserDatabaseConnector";

    private UserServiceConnector connector;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        connector = new UserServiceConnector(super.vertx);
    }

    @Override
    protected String getAddress() {
        return ADDRESS;
    }

    @Action
    public Observable<User> login(UserId userId) {
        return connector.authenticate(userId);

    }

    @Action("get")
    public Observable<User> getUser(UserId userId) {
        return connector.findUser(userId);
    }

    @Action("create")
    public Observable<User> addUser(User user) {
        return connector.addUser(user);
    }
}
