package com.senacor.reactile.service.appointment;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.service.branch.BranchService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import javax.inject.Inject;

public class AppointmentServiceVerticle extends AbstractServiceVerticle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AppointmentService appointmentService;

    @Inject
    public AppointmentServiceVerticle(@Impl AppointmentService appointmentService) {
        super(appointmentService);
        this.appointmentService = appointmentService;
    }

}
