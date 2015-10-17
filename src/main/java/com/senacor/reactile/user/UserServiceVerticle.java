package com.senacor.reactile.user;

import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import rx.Observable;

import javax.inject.Inject;

public class UserServiceVerticle extends AbstractServiceVerticle {

    public static final String ADDRESS = "UserServiceVerticle";

    private final UserConnector connector;

    @Inject
    public UserServiceVerticle(UserConnector connector) {
        this.connector = connector;
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
