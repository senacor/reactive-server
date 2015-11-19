package com.senacor.reactile.service.appointment;

import javax.inject.Inject;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class AppointmentServiceVerticle extends AbstractServiceVerticle {
    @Inject
    public AppointmentServiceVerticle(@Impl AppointmentService appointmentService) {
        super(appointmentService);
    }
}
