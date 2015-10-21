package com.senacor.reactile.bootstrap;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

public class MongoBootstrap extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        launchMongoServiceObservable()
                .subscribe(
                        id -> System.out.println("Mongo started with deploymentId " + id),
                        startFuture::fail,
                        startFuture::complete
                );
    }

    private Observable<String> launchMongoServiceObservable() {
        JsonObject config = new JsonObject()
                .put("db_name", "reactile");
        ObservableFuture<String> observableHandler = RxHelper.observableFuture();
        getVertx().deployVerticle("service:io.vertx.mongo-service", new DeploymentOptions().setConfig(config), observableHandler.toHandler());
        return observableHandler;
    }
}
