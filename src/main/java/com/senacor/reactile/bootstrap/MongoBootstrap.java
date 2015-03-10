package com.senacor.reactile.bootstrap;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.account.AccountFixtures;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerFixtures;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

public class MongoBootstrap extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        launchMongoServiceObservable()
                .subscribe(
                        outcome -> System.out.println("Value " + outcome + " processed."),
                        startFuture::fail,
                        () -> startFuture.complete()

                );
    }

    private Observable<String> launchMongoServiceObservable() {
        JsonObject config = new JsonObject()
                .put("db_name", "reactile");
        ObservableFuture<String> observableHandler = RxHelper.observableFuture();
        getVertx().deployVerticle("service:io.vertx:mongo-service", new DeploymentOptions().setConfig(config), observableHandler.toHandler());
        return observableHandler;
    }

    private Observable<String> writeSomethingObservable() {
        MongoService service = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");

        Customer customer = CustomerFixtures.defaultCustomer();

        ObservableFuture<String> custObservable = RxHelper.observableFuture();
        service.insert("customers", customer.toJson(), custObservable.toHandler());

        Account account_1 = AccountFixtures.newAccount1();

        ObservableFuture<String> acc1observable = RxHelper.observableFuture();
        service.insert("accounts", account_1.toJson(), acc1observable.toHandler());

        Account account_2 = AccountFixtures.newAccount2();
        ObservableFuture<String> acc2observable = RxHelper.observableFuture();
        service.insert("accounts", account_2.toJson(), acc2observable.toHandler());

        return custObservable.concatWith(acc1observable).concatWith(acc2observable);
    }


}
