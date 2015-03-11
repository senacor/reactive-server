package com.senacor.reactile.mongo;

import com.senacor.reactile.account.Account;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rxjava.core.Vertx;

public class MongoInitializer {

    public static final String COLLECTION = "accounts";
    private final ObservableMongoService mongoService;

    public MongoInitializer(ObservableMongoService mongoService) {
        this.mongoService = mongoService;
    }

    public MongoInitializer(Vertx vertx) {
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
