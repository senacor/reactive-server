package com.senacor.reactile.bootstrap;

import com.senacor.reactile.Services;
import com.senacor.reactile.codec.Codecs;
import com.senacor.reactile.gateway.InitialDataVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ApplicationStartup extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<String> deployedIds = new LinkedHashSet<>();

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        startVerticle(MongoBootstrap.class.getName())
                .subscribe(
                        deployedIds::add,
                        startFuture::fail,
                        () -> {
                            logger.info(String.format("Deployed %s verticles with the following deploymentIds: %s", deployedIds.size(), deployedIds));
                            startServices(startFuture);

                        }
                );
    }

    private void startServices(Future<Void> startFuture) {
        services().flatMap(service -> startVerticle(service.getId()))
                .subscribe(
                        deployedIds::add,
                        startFuture::fail,
                        () -> {
                            logger.info(String.format("Deployed %s verticles with the following deploymentIds: %s", deployedIds.size(), deployedIds));
                            initializeData(startFuture);

                        }
                );
    }

    private void initializeData(Future<Void> startFuture) {
        startVerticle("guice:" + InitialDataVerticle.class.getName())
                .subscribe(
                        deployedIds::add,
                        startFuture::fail,
                        () -> {
                            logger.info(String.format("Deployed %s verticles with the following deploymentIds: %s", deployedIds.size(), deployedIds));
                            startFuture.complete();
                        }
                );
    }

    private Observable<Services> services() {
        return Observable.from(EnumSet.range(Services.NewsService, Services.GatewayService));
    }

    private Observable<String> startVerticle(String identifier) {
        return vertx.deployVerticleObservable(identifier)
                .doOnNext(id -> logger.info("Starting verticle with identifier " + identifier + " and deploymentId " + id));
    }

    private Observable<Void> stopVerticle(String deploymentId) {
        logger.info("Stopping verticle with deploymentId " + deploymentId);
        return vertx.undeployObservable(deploymentId);
    }
}
