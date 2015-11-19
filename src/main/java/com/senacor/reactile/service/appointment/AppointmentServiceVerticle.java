package com.senacor.reactile.service.appointment;

import com.google.inject.Inject;
import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;

public class AppointmentServiceVerticle extends AbstractServiceVerticle {

    private AppointmentService service;

    @Inject
    public AppointmentServiceVerticle(@Impl AppointmentService serviceInstance) {
        super(serviceInstance);
        this.service = serviceInstance;
    }
}
