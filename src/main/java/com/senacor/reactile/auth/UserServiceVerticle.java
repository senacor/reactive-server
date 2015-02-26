package com.senacor.reactile.auth;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;
import rx.functions.Action1;

public class UserServiceVerticle extends AbstractVerticle {

    //TODO read from context.config
    public static final String ADDRESS = "UserDatabaseConnector";

    private UserServiceConnector connector;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        connector = new UserServiceConnector(super.vertx);
    }

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(ADDRESS).toObservable().subscribe(
                this::messageHandler,
                errorHandler());
    }

    //this can be frameworked
    private void messageHandler(Message<Object> message) {
        Object payload = message.body();
        String action = message.headers().get("action");
        Observable<? extends Object> serviceResult = null;
        switch (action) {
            case "login": {
                serviceResult = login((UserId) payload);
                break;
            }
            case "get": {
                serviceResult = getUser((UserId) payload);
                break;
            }
            case "create": {
                serviceResult = addUser((User) payload);
                break;
            }
            default: throw new IllegalArgumentException("Unknown service operation " + action);
        }
        serviceResult.subscribe(
                message::reply,
                Throwable::printStackTrace
        );
    }

    //Here goes the service business logic, invocation of connectors and combining results, etc
    private Observable<User> login(UserId userId) {
        return connector.authenticate(userId);

    }

    //Here goes the service business logic, invocation of connectors and combining results, etc
    private Observable<? extends Object> getUser(UserId userId) {
        return connector.findUser(userId);
    }

    //Here goes the service business logic, invocation of connectors and combining results, etc
    private Observable<User> addUser(User user) {
        return connector.addUser(user);
    }

    //TODO error handling
    private Action1<Throwable> errorHandler() {
        return Throwable::printStackTrace;
    }
}
