package com.senacor.reactile.gateway.commands;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.rxjava.service.appointment.AppointmentService;
import com.senacor.reactile.rxjava.service.branch.BranchService;
import com.senacor.reactile.rxjava.service.customer.CustomerService;
import com.senacor.reactile.rxjava.service.user.UserService;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.appointment.AppointmentList;
import com.senacor.reactile.service.branch.Branch;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.user.User;
import com.senacor.reactile.service.user.UserId;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import javax.inject.Inject;

import static com.senacor.reactile.json.JsonObjects.$;

/**
 * @author Mihael Gorupec, Senacor Technologies AG
 */
public class AppointmentsSummaryCommand extends HystrixObservableCommand<JsonObject> {

    private final AppointmentService appointmentService;
    private final CustomerService customerService;
    private final BranchService branchService;
    private final UserService userService;

    @Inject
    public AppointmentsSummaryCommand(AppointmentService appointmentService, CustomerService customerService,
                                      BranchService branchService, UserService userService){

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Gateway"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("Appointments"))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(50))
        );

        this.appointmentService = appointmentService;
        this.customerService = customerService;
        this.branchService = branchService;
        this.userService = userService;
    }

    @Override
    protected Observable<JsonObject> construct() {

        Observable<AppointmentList> appointmentObservable =
                appointmentService.getAllAppointmentsObservable();

        return appointmentObservable.flatMap(appointment -> Observable.from(appointment.getAppointmentList()))
                .flatMap(appointment -> {
                    Observable<User> user = userService.getUserObservable(new UserId(appointment.getUserId()));
                    Observable<Branch> branch = branchService.getBranchObservable(appointment.getBranchId());
                    Observable<Customer> customer = customerService.getCustomerObservable(new CustomerId(
                            appointment.getCustomerId()));
                    return Observable.zip(Observable.just(appointment), branch, user, customer, this::toJson);

                }).toList().map(array -> $().put("appointments", array) );
    }

    private JsonObject toJson(Appointment appointment, Branch branch, User user, Customer customer) {
        return $()
                .put("name", appointment.getName())
                .put("start", appointment.getStart().toString())
                .put("end", appointment.getEnd().toString())
                .put("customer", customer.getFirstname()+" "+customer.getLastname())
                .put("branch", branch.getName())
               .put("user", user.getFirstName() + " " + user.getLastName());
    }

}
