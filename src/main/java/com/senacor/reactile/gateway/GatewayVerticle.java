package com.senacor.reactile.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.account.Account;
import com.senacor.reactile.account.AccountService;
import com.senacor.reactile.account.AccountServiceImpl;
import com.senacor.reactile.account.CreditCard;
import com.senacor.reactile.account.CreditCardService;
import com.senacor.reactile.account.CreditCardServiceImpl;
import com.senacor.reactile.account.Transaction;
import com.senacor.reactile.account.TransactionService;
import com.senacor.reactile.account.TransactionServiceImpl;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.customer.CustomerService;
import com.senacor.reactile.customer.CustomerServiceImpl;
import com.senacor.reactile.json.JsonMarshaller;
import com.senacor.reactile.user.User;
import com.senacor.reactile.user.UserId;
import com.senacor.reactile.user.UserService;
import com.senacor.reactile.user.UserServiceImpl;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rx.java.ObservableHandler;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerRequestStream;
import io.vertx.rxjava.core.http.HttpServerResponse;
import rx.Observable;

import java.util.List;

public class GatewayVerticle extends AbstractVerticle {

    public static final String PUBLISH_ADDRESS = "EventPump";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JsonMarshaller jsonMarshaller = new JsonMarshaller();
    private UserService userService;
    private CustomerService customerService;
    private AccountService accountService;
    private CreditCardService creditCardService;
    private TransactionService transactionService;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        userService = new UserServiceImpl(super.vertx);
        customerService = new CustomerServiceImpl(super.vertx);
        accountService = new AccountServiceImpl(super.vertx);
        creditCardService = new CreditCardServiceImpl(super.vertx);
        transactionService = new TransactionServiceImpl(super.vertx);
    }

    @Override
    public void start() {
        createHttpServer();
        registerEventSubcriber();
    }

    private void createHttpServer() {
        HttpServerOptions options = newServerConfig();
        HttpServer httpServer = vertx.createHttpServer(options);
        addRequestStreamHooks(httpServer);
        ObservableHandler<HttpServerRequest> requestHandler = RxHelper.observableHandler();
        requestHandler
                .flatMap(this::handleRequest)
                .subscribe(
                        response -> response.setStatusCode(200).setStatusMessage("vertx is awesome").end(),
                        Throwable::printStackTrace
                );

        httpServer.requestHandler(requestHandler.toHandler());

        httpServer.listenObservable().subscribe(
                server -> log.info("Listening at " + options.getHost() + ":" + options.getPort()),
                failure -> log.error("Failed to start")
        );
    }

    private void registerEventSubcriber() {
        vertx.eventBus().consumer(PUBLISH_ADDRESS).toObservable()
                .map(message -> (CustomerAddressChangedEvt) message.body())
                .flatMap(event -> {
                    Observable<User> userObservable = userService.getUser(event.getUserId());
                    return userObservable.map(user -> event.replaceUser(user));
                })
                .subscribe(eventWithUser -> log.info("Received event " + eventWithUser));
    }

    private void addRequestStreamHooks(HttpServer httpServer) {
        HttpServerRequestStream requestStream = httpServer.requestStream();
        requestStream.exceptionHandler(this::handleException);
        requestStream.endHandler(this::handleRequestEnd);
    }

    private Observable<HttpServerResponse> handleRequest(HttpServerRequest request) {
        return serveRequest(request, request.response(), request.params());
    }

    private Observable<HttpServerResponse> serveRequest(HttpServerRequest request, HttpServerResponse response, MultiMap params) {
        UserId userId = new UserId(getParam(params, "user"));
        CustomerId customerId = new CustomerId(getParam(params, "customerId"));

        return userService.getUser(userId).flatMap(user -> {
            Observable<Customer> customerObservable = customerService.getCustomer(customerId);
            Observable<List<Account>> accountObservable = accountService.getAccountsForCustomer(customerId);
            Observable<List<CreditCard>> creditCardObservable = creditCardService.getCreditCardsForCustomer(customerId);
            Observable<List<Transaction>> transactionObservable = transactionService.getTransactionsForCustomer(customerId);
            return Observable.zip(customerObservable, accountObservable, creditCardObservable, transactionObservable, (cust, acc, cc, tr) -> new ResponseObject(cust, acc, cc, tr));
        }).map(responseObject -> writeResponse(response, responseObject));
    }

    private HttpServerResponse writeResponse(HttpServerResponse response, ResponseObject responseObject) {
        Buffer content = jsonMarshaller.toBuffer(responseObject);
        response.headers().set("Content-Length", "" + content.length());
        return response.write(content);
    }

    private static String getParam(MultiMap params, String key) {
        return params.get(key);
    }

    private void handleRequestEnd(Void v) {
        log.debug("request stream has been fully read");
    }

    private void handleException(Throwable throwable) {
        log.error(throwable);
    }

    @Override
    public void stop() {
        log.info("Verticle stopped");
    }


    private static HttpServerOptions newServerConfig() {
        return new HttpServerOptions()
                .setHost("localhost")
                .setPort(8080)
                ;
    }

    private static class ResponseObject {

        private final Customer customer;
        private final List<Account> accounts;
        private final List<CreditCard> creditCards;
        private final List<Transaction> transactions;

        public ResponseObject(
                @JsonProperty("customer") Customer customer,
                @JsonProperty("accounts") List<Account> accounts,
                @JsonProperty("creditCards") List<CreditCard> creditCards,
                @JsonProperty("transactions") List<Transaction> transactions) {

            this.customer = customer;
            this.accounts = accounts;
            this.creditCards = creditCards;
            this.transactions = transactions;
        }

        public Buffer toJson() {
            return JsonMarshaller.toBuffer(this);
        }

        public Customer getCustomer() {
            return customer;
        }

        public List<Account> getAccounts() {
            return accounts;
        }

        public List<CreditCard> getCreditCards() {
            return creditCards;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }
    }

}
