package com.senacor.reactile.gateway.commands;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.rxjava.service.appointment.AppointmentService;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.appointment.AppointmentList;
import com.senacor.reactile.service.user.User;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import rx.Observable;
import rx.observables.GroupedObservable;

import javax.inject.Inject;

import static com.senacor.reactile.json.JsonObjects.$;

/**
 * Command to collect the start-page data
 * <p/>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 16.04.15
 * Time: 15:25
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class BranchOverviewCommand extends HystrixObservableCommand<JsonObject> {

  private final AppointmentService appointmentService;

  private final String branchId;

  @Inject
  public BranchOverviewCommand(AppointmentService appointmentService,
                               @Assisted String branchId) {


    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Gateway"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("Branches"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                                                      .withExecutionIsolationStrategy(
                                                                              HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                                                      .withExecutionIsolationSemaphoreMaxConcurrentRequests(
                                                                              50))
         );
    this.appointmentService = appointmentService;
    this.branchId = branchId;
  }

  @Override
  protected Observable<JsonObject> construct() {
      /*

    return branchService.getBranchObservable(branchId).flatMap(branch -> {
      Observable<JsonObject> customerObservable = customerService.getCustomerObservable(customerId)
                                                                 .map(JsonObjects::toJson);
      Observable<JsonArray> accountObservable = accountService.getAccountsForCustomerObservable(customerId)
                                                              .map(JsonArray::new);
      //TODO: by what???
      Observable<JsonObject> branchObservable = branchService.getBranchObservable("1")
                                                             .map(JsonObjects::toJson);
      Observable<JsonArray> creditCardObservable = creditCardService.getCreditCardsForCustomerObservable(customerId)
                                                                    .map(CreditCardList::getCreditCardList)
                                                                    .map(JsonObjects::toJsonArray);
      Observable<JsonArray> transactionObservable = transactionService.getTransactionsForCustomerObservable(customerId)
                                                                      .map(TransactionList::getTransactionList)
                                                                      .map(JsonObjects::toJsonArray);

      return zip(customerObservable, accountObservable, branchObservable, creditCardObservable, transactionObservable,
                 this::mergeIntoResponse);
    });
    */
    return null;
  }

  protected Observable<String> branchOverview() {

    // get appointments, filter older than now
    // group by user id
    // take 10
    // sort by time asc

    final Observable<GroupedObservable<String, Appointment>> appointmentsPerUser =
            appointmentService.getAppointmentsByBranchObservable(branchId)
                              .map(AppointmentList::getAppointmentList).flatMap(Observable::from)
                              .groupBy(Appointment::getUserId).publish().refCount();

    final Observable<GroupedObservable<String, Appointment>> sortedAppointmentsPerUser =
            appointmentsPerUser.toSortedList(
                    (lhs, rhs) -> lhs.getKey().compareTo(rhs.getKey()))
                               .flatMap(Observable::from);

//    final Observable<String> sortedUserIds = appointmentsPerUser.map(GroupedObservable::getKey).toSortedList()
//                                                                   .flatMap(Observable::from);

    final Observable<String> userIdsWithAppointments =
            appointmentsPerUser.map(GroupedObservable::getKey).publish().refCount();


    final Observable<User> branchEmployees = Observable.just(User.aUser().build()).filter(user -> branchId
            .equals(user.getBranchId())).publish().refCount();// missing service call

    //branchEmployees.groupBy(branchEmployee -> userIds.contains(branchEmployee.getId()));
    final Observable<User> employeesWithAppointments =
            branchEmployees.filter(user -> userIdsWithAppointments.contains(user.getId()).toBlocking().first());
    final Observable<User> sortedEmployeesWithAppointments =
            employeesWithAppointments.toSortedList((lhs, rhs) -> lhs.getId().getId().compareTo(rhs.getId().getId()))
                                     .flatMap(Observable::from);
    final Observable<User> employeesWithoutAppointments =
            branchEmployees.filter(user -> !(userIdsWithAppointments.contains(user.getId())).toBlocking().first());

    return Observable.zip(sortedAppointmentsPerUser, sortedEmployeesWithAppointments,
                                                      (appointments, employee) -> employee.getId() + ": " +
                                                                                  appointments.toList().toBlocking()
                                                                                              .first()
                                                                                              .size());


  }


  private JsonObject mergeIntoResponse(JsonObject cust,
                                       JsonArray accounts,
                                       JsonObject branch,
                                       JsonArray creditCards,
                                       JsonArray transactions) {
    return $()
            .put("customer", cust
                    .put("products", $()
                            .put("accounts", accounts)
                            .put("creditCards", creditCards))
                    .put("transactions", transactions))
            .put("branch", branch)
            .put("recommendations", "empty")
            ;
  }
}
