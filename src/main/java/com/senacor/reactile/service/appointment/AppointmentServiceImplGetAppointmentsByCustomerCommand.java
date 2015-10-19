package com.senacor.reactile.service.appointment;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;

/**
 * @author Andreas Keefer
 */
public class AppointmentServiceImplGetAppointmentsByCustomerCommand extends InterceptableHystrixObservableCommand<AppointmentList> {
    public AppointmentServiceImplGetAppointmentsByCustomerCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("AppointmentServiceImplGetAppointmentsByUser"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
    }
}
