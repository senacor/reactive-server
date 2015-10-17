package com.senacor.reactile.appointment;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;

/**
 * Created by mhaunolder on 26.06.15.
 */
public class AppointmentServiceImplGetAllAppointmentsCommand extends InterceptableHystrixObservableCommand<Appointment> {
    public AppointmentServiceImplGetAllAppointmentsCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("AppointmentServiceImplGetAllAppointments"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
    }
}
