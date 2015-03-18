package com.senacor.reactile.creditcard;

import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.service.AbstractServiceVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

public class CreditCardServiceVerticle extends AbstractServiceVerticle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CreditCardService creditCardVerticle;

    @Inject
    public CreditCardServiceVerticle(@Impl CreditCardService creditCardVerticle) {
        this.creditCardVerticle = creditCardVerticle;
    }

    @Override
    public void start() throws Exception {
        log.info("Starting service: " + config().getString("main"));
        String address = config().getString("address");
        if (address == null) {
            throw new IllegalStateException("address field must be specified in config for CustomerService");
        }
        ProxyHelper.registerService(CreditCardService.class, getVertx(), creditCardVerticle, address);
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping service: " + config().getString("main"));
    }
}
