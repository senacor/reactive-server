package com.senacor.reactile.account;

import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rxjava.core.Vertx;

public class AccountMongoInitializer {

    public static final String COLLECTION = "accounts";
    private final ObservableMongoService mongoService;

    public AccountMongoInitializer(ObservableMongoService mongoService) {
        this.mongoService = mongoService;
    }

    public AccountMongoInitializer(Vertx vertx) {
        this.mongoService = ObservableMongoService.from(vertx);
    }

    public void writeBlocking(Account account) {
        write(account)
                .toBlocking()
                .single();
    }

    public ObservableFuture<String> write(Account account) {
        return mongoService.insert(COLLECTION, account.toJson().put("_id", account.getId().toValue()));
    }


}
