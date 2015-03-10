package com.senacor.reactile.mongo;

import com.senacor.reactile.Services;
import com.senacor.reactile.VerticleDeployer;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.Vertx;
import org.junit.rules.ExternalResource;

import java.util.List;


public class EmbeddedMongoRule extends ExternalResource implements ObservableMongoService {

    private final VerticleDeployer verticleDeployer;
    private final MongoService service;

    public EmbeddedMongoRule(Vertx vertx) {
        verticleDeployer = new VerticleDeployer(vertx);
        verticleDeployer.addService(Services.EmbeddedMongo);
        service = MongoService.createEventBusProxy(getVertxDelegate(vertx), "vertx.mongo");
    }

    @Override
    public void before() throws Throwable {
        service.start();
        verticleDeployer.deployVerticles(60 * 1000);
    }

    @Override
    public void after() {
        verticleDeployer.stopVerticles(60 * 1000);
        service.stop();
    }

    @Override
    @Fluent
    public MongoService save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
        return service.save(collection, document, resultHandler);
    }

    public ObservableFuture<String> save(String collection, JsonObject document) {
        return rxify(this::save, collection, document);
    }

    @Override
    @Fluent
    public MongoService saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
        return service.saveWithOptions(collection, document, writeOption, resultHandler);
    }

    @Override
    @Fluent
    public MongoService insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
        return service.insert(collection, document, resultHandler);
    }

    @Override
    public ObservableFuture<String> insert(String collection, JsonObject document) {
        return rxify(this::insert, collection, document);
    }

    @Override
    @Fluent
    public MongoService insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
        return service.insertWithOptions(collection, document, writeOption, resultHandler);
    }

    @Override
    @Fluent
    public MongoService update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler) {
        return service.update(collection, query, update, resultHandler);
    }

    @Override
    @Fluent
    public MongoService updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
        return service.updateWithOptions(collection, query, update, options, resultHandler);
    }

    @Override
    @Fluent
    public MongoService replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler) {
        return service.replace(collection, query, replace, resultHandler);
    }

    @Override
    @Fluent
    public MongoService replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
        return service.replaceWithOptions(collection, query, replace, options, resultHandler);
    }

    @Override
    @Fluent
    public MongoService find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        return service.find(collection, query, resultHandler);
    }

    @Override
    public ObservableFuture<List<JsonObject>> find(String collection, JsonObject query) {
        return rxify(this::find, collection, query);
    }

    @Override
    @Fluent
    public MongoService findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        return service.findWithOptions(collection, query, options, resultHandler);
    }

    @Override
    @Fluent
    public MongoService findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
        return service.findOne(collection, query, fields, resultHandler);
    }

    @Override
    public ObservableFuture<JsonObject> findOne(String collection, JsonObject query) {
        ObservableFuture<JsonObject> observableFuture = RxHelper.observableFuture();
        findOne(collection, query, null, observableFuture.toHandler());
        return observableFuture;
    }

    @Override
    public ObservableFuture<JsonObject> findOne(String collection, JsonObject query, JsonObject fields) {
        ObservableFuture<JsonObject> observableFuture = RxHelper.observableFuture();
        findOne(collection, query, fields, observableFuture.toHandler());
        return observableFuture;
    }

    @Override
    @Fluent
    public MongoService count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
        return service.count(collection, query, resultHandler);
    }

    @Override
    public ObservableFuture<Long> count(String collection, JsonObject query) {
        return rxify(this::count, collection, query);
    }

    @Override
    @Fluent
    public MongoService remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
        return service.remove(collection, query, resultHandler);
    }

    @Override
    public ObservableFuture<Void> remove(String collection, JsonObject query) {
        return rxify(this::remove, collection, query);
    }

    @Override
    @Fluent
    public MongoService removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
        return service.removeWithOptions(collection, query, writeOption, resultHandler);
    }

    @Override
    @Fluent
    public MongoService removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
        return service.removeOne(collection, query, resultHandler);
    }

    @Override
    @Fluent
    public MongoService removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
        return service.removeOneWithOptions(collection, query, writeOption, resultHandler);
    }

    @Override
    @Fluent
    public MongoService createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
        return service.createCollection(collectionName, resultHandler);
    }

    @Override
    @Fluent
    public MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
        return service.getCollections(resultHandler);
    }

    @Override
    @Fluent
    public MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
        return service.dropCollection(collection, resultHandler);
    }

    @Override
    public ObservableFuture<Void> dropCollection(String collection) {
        ObservableFuture<Void> observableFuture = RxHelper.observableFuture();
        dropCollection(collection, observableFuture.toHandler());
        return observableFuture;
    }

    @Override
    @Fluent
    public MongoService runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
        return service.runCommand(command, resultHandler);
    }

    @Override
    public ObservableFuture<JsonObject> runCommand(JsonObject command) {
        ObservableFuture<JsonObject> observableFuture = RxHelper.observableFuture();
        runCommand(command, observableFuture.toHandler());
        return observableFuture;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private io.vertx.core.Vertx getVertxDelegate(Vertx vertx) {
        return (io.vertx.core.Vertx) vertx.getDelegate();
    }

    private static <T> ObservableFuture<T> rxify(MongoOperation<T> mongoOperation, String collection, JsonObject json) {
        ObservableFuture<T> observableFuture = RxHelper.observableFuture();
        mongoOperation.execute(collection, json, observableFuture.toHandler());
        return observableFuture;
    }

    @FunctionalInterface
    private interface MongoOperation<T> {
        MongoService execute(String collection, JsonObject query, Handler<AsyncResult<T>> resultHandler);
    }


}
