package com.senacor.reactile.service.account;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import javax.inject.Inject;

public class TransactionServiceVerticle extends AbstractServiceVerticle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final TransactionService transactionService;

    @Inject
    public TransactionServiceVerticle(@Impl TransactionService transactionService) {
        super(transactionService);
        this.transactionService = transactionService;
    }

}