package com.senacor.reactile.gateway;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.account.CreditCard;
import com.senacor.reactile.auth.User;
import com.senacor.reactile.auth.UserServiceVerticle;
import com.senacor.reactile.auth.UserId;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.customer.CustomerServiceVerticle;
import com.senacor.reactile.json.JsonMarshaller;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rx.java.ObservableHandler;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerRequestStream;
import io.vertx.rxjava.core.http.HttpServerResponse;
import rx.Observable;

import java.math.BigDecimal;

import static com.senacor.reactile.account.Account.anAccount;
import static com.senacor.reactile.account.CreditCard.aCreditCard;

public class GatewayVerticle extends AbstractVerticle {

    public static final String PUBLISH_ADDRESS = "EventPump";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JsonMarshaller jsonMarshaller = new JsonMarshaller();

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

        httpServer.requestHandler(requestHandler.asHandler());

        httpServer.listenObservable().subscribe(
                        server -> log.info("Listening at " + options.getHost() + ":" + options.getPort()),
                        failure -> log.error("Failed to start")
                );
    }

    private void registerEventSubcriber() {
        vertx.eventBus().consumer(PUBLISH_ADDRESS).toObservable()
                .map(message -> (CustomerAddressChangedEvt) message.body())
                .flatMap(event -> {
                    Observable<User> userObservable = getUser(event.getUserId().getId());
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
        String userId = getParam(params, "user");
        String customerId = getParam(params, "customerId");

        return getUser(userId).flatMap(user -> {
            Observable<Customer> customerObservable = getCustomer(customerId);
            Observable<Account> accountObservable = getAccounts(customerId);
            Observable<CreditCard> creditCardObservable = getCreditCards(customerId);
            Observable.zip(customerObservable, accountObservable, creditCardObservable, (cust, acc, cred) -> cust);
            return customerObservable;
        }).map(customer -> {
            Buffer content = jsonMarshaller.toBuffer(customer);
            response.headers().set("Content-Length", "" + content.length());
            return response.write(content);
        });
    }

    private Observable<CreditCard> getCreditCards(String customerId) {
        return Observable.just(aCreditCard()
                .withId("333")
                .withCustomerId(customerId)
                .withBalance(BigDecimal.TEN)
                .withCurrency("EUR")
                .build());
    }

    private Observable<Account> getAccounts(String customerId) {
        return Observable.just(anAccount()
                .withId("333")
                .withCustomerId(customerId)
                .withBalance(BigDecimal.TEN)
                .withCurrency("EUR")
                .build());
    }

    private Observable<User> getUser(String userId) {
        return vertx.eventBus()
                .<User>sendObservable(UserServiceVerticle.ADDRESS, new UserId(userId))
                .map(Message::body);
    }

    private Observable<Customer> getCustomer(String customerId) {
        return vertx.eventBus()
                .<Customer>sendObservable(CustomerServiceVerticle.ADDRESS, new CustomerId(customerId))
                .map(Message::body);
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

}
