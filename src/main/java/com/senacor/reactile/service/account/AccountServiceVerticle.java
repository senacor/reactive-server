package com.senacor.reactile.service.account;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

public class AccountServiceVerticle extends AbstractServiceVerticle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AccountService accountService;

    @Inject
    public AccountServiceVerticle(@Impl AccountService accountService) {
        super(accountService);
        this.accountService = accountService;
    }

}
