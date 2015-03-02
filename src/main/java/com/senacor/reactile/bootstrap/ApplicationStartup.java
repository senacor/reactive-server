package com.senacor.reactile.bootstrap;

import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartup extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.deployVerticleObservable(MongoBootstrap.class.getName()).subscribe(
                outcome -> {
                    System.out.println("all verticles started and dummy-data written");
                    startFuture.complete();
                }, Throwable::printStackTrace);

    }
}
