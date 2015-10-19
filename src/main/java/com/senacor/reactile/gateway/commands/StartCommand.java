package com.senacor.reactile.gateway.commands;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.service.account.TransactionService;
import com.senacor.reactile.service.appointment.Branch;
import com.senacor.reactile.service.creditcard.CreditCardService;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.json.JsonObjects;
import com.senacor.reactile.service.newsticker.News;
import com.senacor.reactile.rxjava.account.AccountService;
import com.senacor.reactile.rxjava.appointment.AppointmentService;
import com.senacor.reactile.rxjava.appointment.BranchService;
import com.senacor.reactile.rxjava.customer.CustomerService;
import com.senacor.reactile.rxjava.newsticker.NewsService;
import com.senacor.reactile.user.UserId;
import com.senacor.reactile.user.UserService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import javax.inject.Inject;
import java.util.stream.Collectors;

import static com.senacor.reactile.json.JsonObjects.$;
import static rx.Observable.zip;

/**
 * Command to collect the start-page data
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 16.04.15
 * Time: 15:25
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class StartCommand extends HystrixObservableCommand<JsonObject> {


    private final UserService userService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final TransactionService transactionService;
    private final AppointmentService appointmentService;
    private final BranchService branchService;
    private final NewsService newsService;

    private final UserId userId;
    private final CustomerId customerId;

    @Inject
    public StartCommand(UserService userService,
                        CustomerService customerService,
                        AccountService accountService,
                        CreditCardService creditCardService,
                        TransactionService transactionService,
                        AppointmentService appointmentService,
                        BranchService branchService,
                        NewsService newsService,
                        @Assisted UserId userId,
                        @Assisted CustomerId customerId) {


        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Start"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.userService = userService;
        this.customerService = customerService;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.transactionService = transactionService;
        this.appointmentService = appointmentService;
        this.branchService = branchService;
        this.newsService = newsService;
        this.userId = userId;
        this.customerId = customerId;
    }

    @Override
    protected Observable<JsonObject> construct() {
        return userService.getUser(userId).flatMap(user -> {
            Observable<JsonObject> customerObservable = customerService.getCustomerObservable(customerId)
                    .map(JsonObjects::toJson);
            Observable<JsonArray> accountObservable = accountService.getAccountsForCustomerObservable(customerId)
                    .map(JsonArray::new);
            Observable<JsonArray> creditCardObservable = creditCardService.getCreditCardsForCustomer(customerId)
                    .map(JsonObjects::toJsonArray);
            Observable<JsonArray> transactionObservable = transactionService.getTransactionsForCustomer(customerId)
                    .map(JsonObjects::toJsonArray);

            Observable<JsonArray> userAppointments = appointmentService.getAppointmentsByCustomerObservable(customerId.getId())
                    .flatMap(appointmentList -> branchService.findBranchesObservable(appointmentList.getAppointmentList().stream()
                            .map(appointment -> appointment.getBranchId())
                            .collect(Collectors.toList()))
                            .flatMap(branchList -> Observable.from(branchList.getBranches()))
                            .map(Branch::toJson)
                            .zipWith(appointmentList.getAppointmentList(), (branchJ, appointment) -> {
                                JsonObject appointmentJ = appointment.toJson();
                                JsonObject appointmentWithBranch = appointmentJ.put("branch", branchJ);
                                appointmentWithBranch.remove("branchId");
                                return appointmentWithBranch;
                            }))
                    .reduce(new JsonArray(), JsonArray::add);

            Observable<JsonArray> newsObservable = newsService.getLatestNewsObservable(10)
                    .flatMap(newsCollection -> Observable.from(newsCollection.getNews()))
                    .map(News::toJson).toList().map(JsonArray::new);

            return zip(customerObservable, accountObservable, creditCardObservable, transactionObservable,
                    userAppointments, newsObservable, this::mergeIntoResponse);
        });
    }


    private JsonObject mergeIntoResponse(JsonObject cust,
                                         JsonArray accounts,
                                         JsonArray creditCards,
                                         JsonArray transactions,
                                         JsonArray userAppointments,
                                         JsonArray newsObservable) {
        return $()
                .put("customer", cust
                        .put("products", $()
                                .put("accounts", accounts)
                                .put("creditCards", creditCards))
                        .put("transactions", transactions))
                .put("branch", "empty")
                .put("appointments", userAppointments)
                .put("recommendations", "empty")
                .put("news", newsObservable)
                ;
    }
}
