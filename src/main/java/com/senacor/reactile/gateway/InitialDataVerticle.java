package com.senacor.reactile.gateway;

import com.senacor.reactile.account.AccountService;
import com.senacor.reactile.account.AccountServiceImpl;
import com.senacor.reactile.account.CreditCardService;
import com.senacor.reactile.account.CreditCardServiceImpl;
import com.senacor.reactile.account.TransactionService;
import com.senacor.reactile.account.TransactionServiceImpl;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.customer.CustomerService;
import com.senacor.reactile.customer.CustomerServiceImpl;
import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import java.util.stream.Stream;

import static io.vertx.rxjava.core.RxHelper.scheduler;

public class InitialDataVerticle extends AbstractVerticle {

    public static final int COUNT = 100;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private InitialData initialData;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        CustomerService customerService = new CustomerServiceImpl(super.vertx);
        AccountService accountService = new AccountServiceImpl(super.vertx);
        CreditCardService creditCardService = new CreditCardServiceImpl(super.vertx);
        TransactionService transactionService = new TransactionServiceImpl(super.vertx);
        initialData = new InitialData(super.vertx, customerService, accountService, creditCardService, transactionService);
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
