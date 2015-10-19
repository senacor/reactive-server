package com.senacor.reactile.service.account;

import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.mongo.MongoService;
import io.vertx.serviceproxy.ProxyHelper;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TransactionServiceVerticle extends AbstractVerticle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final TransactionService transactionService;

    @Inject
    public TransactionServiceVerticle(@Impl TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void start() throws Exception {
        log.info("Starting service: " + config().getString("main"));
        String address = config().getString("address");
        if (address == null) {
            throw new IllegalStateException("address field must be specified in config for CustomerService");
        }
        ProxyHelper.registerService(TransactionService.class, getVertx(), transactionService, address);
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping service: " + config().getString("main"));
    }
}