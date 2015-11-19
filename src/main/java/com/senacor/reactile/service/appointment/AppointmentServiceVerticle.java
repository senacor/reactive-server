package com.senacor.reactile.service.appointment;

import javax.inject.Inject;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class AppointmentServiceVerticle extends AbstractServiceVerticle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AppointmentService appointmentService;

    @Inject
    public AppointmentServiceVerticle(@Impl AppointmentService appointmentService) {
        super(appointmentService);
        this.appointmentService = appointmentService;
    }
}
