package com.senacor.reactile.bootstrap;

import com.senacor.reactile.Services;
import com.senacor.reactile.codec.Codecs;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ApplicationStartup extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<String> deployedIds = new LinkedHashSet<>();

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        registerCodecs();

        Observable<String> mongoBootstrapObservable = startVerticle(MongoBootstrap.class.getName());
        services().map(service -> startVerticle(service.getId()))
                .reduce(Observable::concat).get()
                .concatWith(mongoBootstrapObservable)
                .subscribe(
                        deployedIds::add,
                        startFuture::fail,
                        startFuture::complete
                );
    }

    private void registerCodecs() {
        Codecs.load(getVertx().eventBus());
    }

    private Observable<String> startVerticle(String identifier) {
        return vertx.deployVerticleObservable(identifier)
                .doOnNext(id -> logger.info("Starting verticle with identifier " + identifier + " and deploymentId " + id));
    }

    private Observable<Void> stopVerticle(String deploymentId) {
        logger.info("Stopping verticle with deploymentId " + deploymentId);
        return vertx.undeployObservable(deploymentId);
    }


    private Stream<Services> services() {
        return Stream.of(
                Services.CustomerService,
                Services.AccountService,
                Services.CreditCardService,
                Services.TransactionService,
                Services.UserService
        );
    }
}
