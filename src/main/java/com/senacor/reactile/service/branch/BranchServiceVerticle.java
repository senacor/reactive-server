package com.senacor.reactile.service.branch;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

public class BranchServiceVerticle extends AbstractServiceVerticle {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final BranchService branchService;

  @Inject
  public BranchServiceVerticle(@Impl BranchService branchService) {
    super(branchService);
    this.branchService = branchService;
  }

}
