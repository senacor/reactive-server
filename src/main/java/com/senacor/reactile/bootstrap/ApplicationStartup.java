package com.senacor.reactile.bootstrap;

import com.senacor.reactile.Services;
import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ApplicationStartup extends AbstractVerticle {

    private final Set<String> deployedIds = new LinkedHashSet<>();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        startVerticle(MongoBootstrap.class.getName());
        services().map(service -> startVerticle(service.getId()))
                .reduce(Observable::concat).get()
                .subscribe(
                        deployedIds::add,
                        startFuture::fail,
                        startFuture::complete
                );
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        deployedIds.stream().map(this::stopVerticle).reduce(Observable::concat).get()
                .subscribe(
                        res -> {
                        },
                        stopFuture::fail,
                        stopFuture::complete
                );
    }

    private Observable<String> startVerticle(String identifier) {
        return vertx.deployVerticleObservable(identifier);
    }

    private Observable<Void> stopVerticle(String deploymentId) {
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
