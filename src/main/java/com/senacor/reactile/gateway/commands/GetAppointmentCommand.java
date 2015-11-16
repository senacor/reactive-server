package com.senacor.reactile.gateway.commands;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.appointment.AppointmentService;
import io.vertx.core.json.JsonObject;
import rx.Observable;

/**
 */
public class GetAppointmentCommand extends HystrixObservableCommand<JsonObject> {

    private final AppointmentService appointmentService;
    private final String appointmentId;

    @Inject
    public GetAppointmentCommand(AppointmentService appointmentService, @Assisted String appointmentId) {

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Gateway"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetAppointment"))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(50))
        );
        this.appointmentService = appointmentService;
        this.appointmentId = appointmentId;
    }

    @Override
    protected Observable<JsonObject> construct() {
        return appointmentService.getAppointmentById(appointmentId)
                .map(Appointment::toJson);
    }

}
