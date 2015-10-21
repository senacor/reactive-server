package com.senacor.reactile.mongo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

public class EmbeddedMongoVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmbeddedMongo embeddedMongo = new EmbeddedMongo();

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        if (vertx != null && !context.isWorker()) {
            throw new IllegalStateException("Must be started as worker verticle!");
        }

        JsonObject config = context.config();

        int port = config.getInteger("port");

        try {
            embeddedMongo.start(port);
        } catch (Throwable throwable) {
            logger.error(throwable);
            startFuture.fail(throwable);
        }

        launchMongoServiceObservable()
                .subscribe(
                        res -> {
                        },
                        startFuture::fail,
                        startFuture::complete
                );
    }

    @Override
    public void stop() throws Exception {
        embeddedMongo.stop();
    }


    private Observable<String> launchMongoServiceObservable() {
        JsonObject config = new JsonObject()
                .put("db_name", "reactile");
        ObservableFuture<String> observableHandler = RxHelper.observableFuture();
        getVertx().deployVerticle("service:io.vertx.mongo-service", new DeploymentOptions().setConfig(config), observableHandler.toHandler());
        return observableHandler;
    }
}
