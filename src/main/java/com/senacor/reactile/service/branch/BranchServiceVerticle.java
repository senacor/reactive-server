package com.senacor.reactile.service.branch;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.service.customer.CustomerService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import javax.inject.Inject;


public class BranchServiceVerticle extends AbstractServiceVerticle {
    @Inject
    public BranchServiceVerticle(@Impl BranchService branchService) {
        super(branchService);
    }

}
