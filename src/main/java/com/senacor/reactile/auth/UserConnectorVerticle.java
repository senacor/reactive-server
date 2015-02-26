package com.senacor.reactile.auth;

import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import rx.Observable;

public class UserConnectorVerticle extends AbstractServiceVerticle {

    public final static String ADDRESS = "UserConnectorVerticle";

    private final UserDatabase database = new UserDatabase();

    @Action
    public Observable<User> login(UserId userId) {
        return Observable.just(database.login(userId));

    }

    @Action
    public Observable<User> findOne(UserId userId) {
        return Observable.just(database.findUser(userId));
    }

    @Action
    public Observable<User> add(User user) {
        database.addUser(user);
        return Observable.just(user);
    }

    @Override
    protected String getAddress() {
        return ADDRESS;
    }


}
