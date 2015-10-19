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

    private final UserId userId;
    private final CustomerId customerId;

    @Inject
    public StartCommand(UserService userService,
                        CustomerService customerService,
                        AccountService accountService,
                        CreditCardService creditCardService,
                        TransactionService transactionService,
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

            return zip(customerObservable, accountObservable, creditCardObservable, transactionObservable, this::mergeIntoResponse);
        });
    }


    private JsonObject mergeIntoResponse(JsonObject cust,
                                         JsonArray accounts,
                                         JsonArray creditCards,
                                         JsonArray transactions) {
        return $()
                .put("customer", cust
                        .put("products", $()
                                .put("accounts", accounts)
                                .put("creditCards", creditCards))
                        .put("transactions", transactions))
                .put("branch", "empty")
                .put("recommendations", "empty")
                ;
    }
}
