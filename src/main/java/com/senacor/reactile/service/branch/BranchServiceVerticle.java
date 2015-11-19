package com.senacor.reactile.service.branch;

import com.google.inject.Inject;
import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.service.customer.CustomerService;
import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Created by hannes on 19/11/15.
 */
public class BranchServiceVerticle extends AbstractServiceVerticle {

    private final BranchService branchService;

    @Inject
    public BranchServiceVerticle(@Impl BranchService serviceInstance) {
        super(serviceInstance);
        this.branchService = serviceInstance;
    }
}
