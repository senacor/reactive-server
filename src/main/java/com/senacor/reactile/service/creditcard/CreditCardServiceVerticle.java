package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

public class CreditCardServiceVerticle extends AbstractServiceVerticle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CreditCardService creditCardVerticle;

    @Inject
    public CreditCardServiceVerticle(@Impl CreditCardService creditCardVerticle) {
        super(creditCardVerticle);
        this.creditCardVerticle = creditCardVerticle;
    }
}
