package com.senacor.reactile.gateway.commands;

import static com.senacor.reactile.json.JsonObjects.$;
import static rx.Observable.zip;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.json.JsonObjects;
import com.senacor.reactile.json.JsonizableList;
import com.senacor.reactile.service.account.AccountService;
import com.senacor.reactile.service.account.TransactionList;
import com.senacor.reactile.service.account.TransactionService;
import com.senacor.reactile.service.appointment.AppointmentList;
import com.senacor.reactile.service.appointment.AppointmentService;
import com.senacor.reactile.service.creditcard.CreditCardList;
import com.senacor.reactile.service.creditcard.CreditCardService;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.customer.CustomerService;
import com.senacor.reactile.service.newsticker.NewsCollection;
import com.senacor.reactile.service.newsticker.NewsService;
import com.senacor.reactile.service.user.UserId;
import com.senacor.reactile.service.user.UserService;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import rx.Observable;

/**
 * Command to collect the start-page data
 * <p/>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 16.04.15
 * Time: 15:25
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class StartCommand extends HystrixObservableCommand<JsonObject> {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final TransactionService transactionService;
    private final NewsService newsService;

    private final UserId userId;
    private final CustomerId customerId;

    @Inject
    public StartCommand(AppointmentService appointmentService, UserService userService, CustomerService customerService,
        AccountService accountService, CreditCardService creditCardService, TransactionService transactionService, NewsService newsService,
        @Assisted UserId userId, @Assisted CustomerId customerId) {

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Gateway")).andCommandKey(HystrixCommandKey.Factory.asKey("Start"))
            .andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                    .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.customerService = customerService;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.transactionService = transactionService;
        this.newsService = newsService;
        this.userId = userId;

        this.customerId = customerId;
    }

    @Override
    protected Observable<JsonObject> construct() {
        return userService.getUser(userId).flatMap(user -> {
            Observable<JsonObject> customerObservable = customerService.getCustomer(customerId)
                    .map(JsonObjects::toJson);
            Observable<JsonArray> accountObservable = accountService.getAccountsForCustomer(customerId)
                    .map(JsonizableList::toList).map(JsonArray::new);
            //            Observable<JsonObject> branchObservable = branchService.getBranch("1")
            //                    .map(JsonObjects::toJson);
            Observable<JsonArray> creditCardObservable = creditCardService.getCreditCardsForCustomer(customerId)
                    .map(CreditCardList::getCreditCardList)
                    .map(JsonObjects::toJsonArray);
            Observable<JsonArray> transactionObservable = transactionService.getTransactionsForCustomer(customerId)
                    .map(TransactionList::getTransactionList)
                    .map(JsonObjects::toJsonArray);
            Observable<JsonArray> appointmentObservable = appointmentService.getAppointmentsByCustomer(customerId.getId())
                    .map(AppointmentList::getAppointmentList)
                    .map(JsonObjects::toJsonArray);
            Observable<JsonArray> newsObservable = newsService.getLatestNews(20).map(NewsCollection::getNews).map(JsonObjects::toJsonArray);

            return zip(customerObservable, accountObservable, creditCardObservable, transactionObservable, appointmentObservable, newsObservable,
                this::mergeIntoResponse);
        });
    }

    private JsonObject mergeIntoResponse(JsonObject cust, JsonArray accounts, JsonArray creditCards, JsonArray transactions, JsonArray appointments,
        JsonArray news) {
        return $()
                .put("customer", cust
                        .put("products", $()
                                .put("accounts", accounts)
                                .put("creditCards", creditCards))
                        .put("transactions", transactions))
               // .put("branch", branch)
                .put("appointments", appointments)
                .put("news", news);
    }
}
