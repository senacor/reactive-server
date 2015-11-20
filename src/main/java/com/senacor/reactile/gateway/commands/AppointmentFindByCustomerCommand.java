package com.senacor.reactile.gateway.commands;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.json.Jsonizable;
import com.senacor.reactile.service.appointment.AppointmentList;
import com.senacor.reactile.service.appointment.AppointmentService;
import com.senacor.reactile.service.customer.CustomerId;

import io.vertx.core.json.JsonObject;
import rx.Observable;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class AppointmentFindByCustomerCommand extends HystrixObservableCommand<List<JsonObject>> {

    private final AppointmentService appointmentService;
    private CustomerId customerId;

    @Inject
    public AppointmentFindByCustomerCommand(AppointmentService appointmentService, @Assisted CustomerId customerId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Service"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("FindAppointmentByCustomer"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.appointmentService = appointmentService;
        this.customerId = customerId;
    }

    @Override
    protected Observable<List<JsonObject>> construct() {
        return appointmentService.getAppointmentsByCustomer(customerId.getId())
            .map(AppointmentList::getAppointmentList)
            .map(this::convertToJsonObject);
    }

    private List<JsonObject> convertToJsonObject(List<? extends Jsonizable> list) {
        return list.stream().map(Jsonizable::toJson).collect(Collectors.toList());
    }
}
