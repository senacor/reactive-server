package com.senacor.reactile.service.appointment;


import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

/**
 * @author Mihael Gorupec, Senacor Technologies AG
 */
public class AppointmentServiceVerticle extends AbstractServiceVerticle {

    private final AppointmentService appointmentService;

    @Inject
    public AppointmentServiceVerticle(@Impl AppointmentService appointmentService) {
        super(appointmentService);
        this.appointmentService = appointmentService;
    }

}
