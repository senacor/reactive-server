package com.senacor.reactile.mongo;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rxjava.core.Vertx;

public class MongoInitializer {

    private final ObservableMongoService mongoService;
    private final String collection;

    public MongoInitializer(ObservableMongoService mongoService, String collection) {
        this.mongoService = mongoService;
        this.collection = collection;
    }

    public MongoInitializer(Vertx vertx, String collection) {
        this(ObservableMongoService.from(vertx), collection);
    }

    public void writeBlocking(Jsonizable account) {
        write(account)
                .toBlocking()
                .single();
    }

    public ObservableFuture<String> write(Jsonizable account) {
        return mongoService.insert(collection, account.toJson());
    }


}
