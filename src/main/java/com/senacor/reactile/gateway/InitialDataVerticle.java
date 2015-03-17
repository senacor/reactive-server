package com.senacor.reactile.gateway;

import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import javax.inject.Inject;
import java.util.stream.Stream;

import static io.vertx.rxjava.core.RxHelper.scheduler;

public class InitialDataVerticle extends AbstractVerticle {

    public static final int COUNT = 100;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InitialData initialData;

    @Inject
    public InitialDataVerticle(InitialData initialData) {
        this.initialData = initialData;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        initialData.initialize(generateCustomerIds())
                .subscribeOn(scheduler(vertx))
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
        ObservableMongoService mongoService = ObservableMongoService.from(vertx);
        Stream
                .of("customers, accounts, transactions, creditcards")
                .map(coll -> mongoService.dropCollection(coll).asObservable())
                .reduce(Observable::concat).get()
                .subscribeOn(scheduler(vertx))
                .doOnCompleted(stopFuture::complete)
                .doOnError(stopFuture::fail)
                .subscribe();

    }

    private Observable<CustomerId> generateCustomerIds() {
        return Observable.range(100_000, COUNT).map(id -> new CustomerId("cust-" + id));
    }
}
