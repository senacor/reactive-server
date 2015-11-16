package com.senacor.reactile.service.customer;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import javax.inject.Inject;


public class CustomerServiceVerticle extends AbstractServiceVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CustomerService customerService;

    @Inject
    public CustomerServiceVerticle(@Impl CustomerService customerService) {
        super(customerService);
        this.customerService = customerService;
    }

}
