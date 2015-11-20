package com.senacor.reactile.gateway.commands;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.appointment.AppointmentService;

import rx.Observable;

/**
 * Histrix command to create an appointment
 *
 * @author Andreas Karoly, Senacor Technologies AG
 */
public class AppointmentCreationCommand extends HystrixObservableCommand<Appointment> {

    private final AppointmentService appointmentService;
    private final Appointment appointment;

    @Inject
    public AppointmentCreationCommand(AppointmentService appointmentService,
            @Assisted Appointment appointment) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("CreateAppointment"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.appointment = appointment;
        this.appointmentService = appointmentService;
    }

    @Override
    protected Observable<Appointment> construct() {
        return appointmentService.createOrUpdateAppointment(appointment);
    }
}
