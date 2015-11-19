package com.senacor.reactile.service.appointment;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by sbode on 19.11.15.
 */
public class AppointmentServiceVerticle extends AbstractServiceVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    public AppointmentServiceVerticle(@Impl AppointmentService appointmentService) {
        super(appointmentService);
    }
}
