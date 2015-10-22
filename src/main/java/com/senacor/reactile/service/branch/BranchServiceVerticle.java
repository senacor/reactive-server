package com.senacor.reactile.service.branch;

import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

public class BranchServiceVerticle extends AbstractVerticle {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final BranchService branchService;

  @Inject
  public BranchServiceVerticle(@Impl BranchService branchService) {
    this.branchService = branchService;
  }

  @Override
  public void start() throws Exception {
    log.info("Starting service Verticle: " + config().getString("address"));
    String address = config().getString("address");
    if (address == null) {
      throw new IllegalStateException("address field must be specified in config for CustomerService");
    }
    ProxyHelper.registerService(BranchService.class, getVertx(), branchService, address);
  }

  @Override
  public void stop() throws Exception {
    log.info("Stopping service Verticle: " + config().getString("address"));
  }
}
