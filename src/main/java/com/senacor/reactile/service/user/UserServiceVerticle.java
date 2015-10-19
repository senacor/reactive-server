package com.senacor.reactile.service.user;

import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.service.AbstractServiceVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

public class UserServiceVerticle extends AbstractServiceVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;

    @Inject
    public UserServiceVerticle(@Impl UserService userService) {
        this.userService = userService;
    }

    @Override
    public void start() throws Exception {
        String address = config().getString("address");
        if (address == null) {
            throw new IllegalStateException("address field must be specified in config for CustomerService");
        }
        ProxyHelper.registerService(UserService.class, getVertx(), userService, address);
    }

}
