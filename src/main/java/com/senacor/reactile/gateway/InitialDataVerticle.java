package com.senacor.reactile.gateway;

import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;
import java.util.stream.Stream;

import static io.vertx.rxjava.core.RxHelper.scheduler;

public class InitialDataVerticle extends AbstractVerticle {

    public static final int COUNT = 100;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InitialData initialData;
    private final MongoService mongoService;

    @Inject
    public InitialDataVerticle(InitialData initialData, MongoService mongoService) {
        this.initialData = initialData;
        this.mongoService = mongoService;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        initialData.initialize(generateCustomerIds())
                .subscribe(
                        id -> logger.info("Generated customer with " + id),
                        throwable -> {
                            logger.error("Error generating customer: " + throwable);
                            startFuture.fail(throwable);
                        },
                        () -> {
                            logger.info("Finished generating " + COUNT + " customers");
                            startFuture.complete();
                        }
                );
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        Stream
                .of("customers, accounts, transactions, creditcards")
                .map(coll -> mongoService.dropCollectionObservable(coll).asObservable())
                .reduce(Observable::concat).get()
                .doOnCompleted(stopFuture::complete)
                .doOnError(stopFuture::fail)
                .subscribe();

    }

    private Observable<CustomerId> generateCustomerIds() {
        return Observable.range(100_000, COUNT).map(id -> new CustomerId("cust-" + id));
    }
}
