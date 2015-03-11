package com.senacor.reactile.mongo;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.ObservableFuture;

import java.util.List;

public interface ObservableMongoService extends MongoService {

    ObservableFuture<String> save(String collection, JsonObject document);

    ObservableFuture<String> insert(String collection, JsonObject document);

    ObservableFuture<List<JsonObject>> find(String collection, JsonObject query);

    ObservableFuture<JsonObject> findOne(String collection, JsonObject query);

    ObservableFuture<JsonObject> findOne(String collection, JsonObject query, JsonObject fields);

    ObservableFuture<Long> count(String collection, JsonObject query);

    ObservableFuture<Void> remove(String collection, JsonObject query);

    ObservableFuture<Void> dropCollection(String collection);

    ObservableFuture<JsonObject> runCommand(JsonObject command);

    static ObservableMongoService from(MongoService mongoService) {
        return new ObservableMongoServiceImpl(mongoService);
    }

    static ObservableMongoService from(Vertx vertx) {
        return new ObservableMongoServiceImpl(MongoService.createEventBusProxy(vertx, "vertx.mongo"));
    }

    static ObservableMongoService from(io.vertx.rxjava.core.Vertx vertx) {
        return from((Vertx) vertx.getDelegate());
    }

}
