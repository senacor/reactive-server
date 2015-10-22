package com.senacor.reactile.service.appointment;


import com.senacor.reactile.guice.Impl;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

/**
 * @author Mihael Gorupec, Senacor Technologies AG
 */
public class AppointmentServiceVerticle extends AbstractVerticle {

    private final AppointmentService appointmentService;

    @Inject
    public AppointmentServiceVerticle(@Impl AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    public void start() throws Exception {
        String address = config().getString("address");
        if (address == null) {
            throw new IllegalStateException("address field must be specified in config for CustomerService");
        }
        ProxyHelper.registerService(AppointmentService.class, getVertx(), appointmentService, address);
    }

}
