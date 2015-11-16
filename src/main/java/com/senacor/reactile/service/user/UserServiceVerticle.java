package com.senacor.reactile.service.user;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

public class UserServiceVerticle extends AbstractServiceVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;

    @Inject
    public UserServiceVerticle(@Impl UserService userService) {
        super(userService);
        this.userService = userService;
    }

}
