package com.senacor.reactile.auth;

import io.vertx.core.Handler;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;

public class UserDatabaseConnector extends AbstractVerticle {

    private final UserDatabase database = new SimpleUserDatabase();

    public static final String ADDRESS = "UserDatabaseConnector";

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(ADDRESS, replyWithCustomer());
    }

    private Handler<Message<Object>> replyWithCustomer() {
        return message -> {
            message.reply(database.getUser((UserId) message.body()));
        };
    }
}
