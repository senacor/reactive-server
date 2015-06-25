package com.senacor.reactile.appointment;

import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;


public class AppointmentServiceVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AppointmentService appointmentService;

    @Inject
    public AppointmentServiceVerticle(@Impl AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    public void start() throws Exception {
        log.info("Starting service Verticle: " + config().getString("address"));
        String address = config().getString("address");
        if (address == null) {
            throw new IllegalStateException("address field must be specified in config for AppointmentService");
        }
        ProxyHelper.registerService(AppointmentService.class, getVertx(), appointmentService, address);
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping service Verticle: " + config().getString("address"));
    }

}
