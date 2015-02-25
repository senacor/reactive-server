package com.senacor.reactile.auth;

import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

public class UserDatabaseService extends AbstractVerticle {

    public static final String ADDRESS = "UserDatabaseConnector";

    private final UserDatabase database = new SimpleUserDatabase();
    private final long latency = 35;

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(ADDRESS).toObservable().delay(latency, TimeUnit.MILLISECONDS).subscribe(replyWithCustomer());
    }

    private Action1<Message<Object>> replyWithCustomer() {
        return message -> {
            message.replyAddress();
            message.reply(database.getUser((UserId) message.body()));
        };
    }
}
