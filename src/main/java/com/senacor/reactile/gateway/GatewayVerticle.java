package com.senacor.reactile.gateway;

import com.senacor.reactile.account.TransactionService;
import com.senacor.reactile.creditcard.CreditCardService;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.json.JsonObjects;
import com.senacor.reactile.rxjava.account.AccountService;
import com.senacor.reactile.rxjava.customer.CustomerService;
import com.senacor.reactile.user.UserId;
import com.senacor.reactile.user.UserService;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.handler.sockjs.BridgeOptions;
import io.vertx.ext.apex.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerRequestStream;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.apex.Router;
import io.vertx.rxjava.ext.apex.RoutingContext;
import io.vertx.rxjava.ext.apex.handler.BodyHandler;
import io.vertx.rxjava.ext.apex.handler.ResponseTimeHandler;
import io.vertx.rxjava.ext.apex.handler.StaticHandler;
import io.vertx.rxjava.ext.apex.handler.TimeoutHandler;
import io.vertx.rxjava.ext.apex.handler.sockjs.SockJSHandler;
import rx.Observable;
import rx.Scheduler;

import javax.inject.Inject;

import static com.senacor.reactile.json.JsonObjects.$;
import static rx.Observable.zip;

public class GatewayVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(GatewayVerticle.class);

    private final UserService userService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final TransactionService transactionService;
    private final Scheduler scheduler;

    @Inject
    public GatewayVerticle(
            UserService userService,
            CustomerService customerService,
            AccountService accountService,
            CreditCardService creditCardService,
            TransactionService transactionService, Scheduler scheduler) {
        this.userService = userService;
        this.customerService = customerService;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.transactionService = transactionService;
        this.scheduler = scheduler;
    }

    @Override
    public void start() {
        createRxRouter();
    }

    private void createRxRouter() {
        Router router = Router.router(vertx);

        // Export Eventbus
        BridgeOptions bridgeOptions = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddressRegex(PushNotificationVerticle.PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE + ".*"));

        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(bridgeOptions));

        // common handler:
        router.route().handler(TimeoutHandler.create(config().getLong("timeout")))
                .handler(ResponseTimeHandler.create());

        // the request body should only be handled on put and post
        router.route().method(HttpMethod.POST).method(HttpMethod.PUT)
                .handler(BodyHandler.create());

        router.route("/customer/:customerId/addresses")
                .method(HttpMethod.POST).method(HttpMethod.PUT)
                .handler(this::handleUpdateAddress);
        router.get("/start").handler(routingContext -> serveRequest(routingContext.request(), routingContext.response(), routingContext.request().params())
                .subscribe(
                        response -> response.setStatusCode(200).setStatusMessage("vertx is awesome"),
                        Throwable::printStackTrace));

        // common handler:
        router.route().handler(this::contentTypeJson)
                .handler(StaticHandler.create())
                .handler(this::end);

        HttpServerOptions serverOptions = newServerConfig();
        HttpServer httpServer = vertx.createHttpServer(serverOptions);
        addRequestStreamHooks(httpServer);
        httpServer.requestHandler(router::accept)
                .listenObservable()
                .subscribe(server -> logger.info("Router Listening at " + serverOptions.getHost() + ":" + serverOptions.getPort()),
                        failure -> logger.error("Router Failed to start: " + failure));

        // Publish a message to the address "news-feed" every second
        vertx.setPeriodic(1000, t -> vertx.eventBus().publish("news-feed", "news from the server!"));
        vertx.setPeriodic(4000, t -> vertx.eventBus().publish("news-feed-x", "XXX news from the server!"));
    }

    private void contentTypeJson(RoutingContext context) {
        context.response().putHeader("content-type", "application/json");
    }

    private void end(RoutingContext context) {
        context.response().end();
    }

    private void handleUpdateAddress(RoutingContext routingContext) {
        String customerIdString = routingContext.request().getParam("customerId");
        HttpServerResponse response = routingContext.response();
        if (customerIdString == null) {
            logger.warn("Request Param :customerId is null");
            sendError(400, "missing customerId parameter", response);
        } else {
            CustomerId customerId = new CustomerId(customerIdString);
            logger.info("body: " + routingContext.getBodyAsString());
            JsonObject newAddressJson = routingContext.getBodyAsJson();
            if (newAddressJson == null) {
                logger.warn("body is null");
                sendError(400, "body is null", response);
            } else {
                Address newAddress = Address.fromJson(newAddressJson);
                customerService.updateAddressObservable(customerId, newAddress)
                        .map(customer -> writeResponse(response, customer.toJson()))
                        .subscribe();
            }
        }
    }

    private void addRequestStreamHooks(HttpServer httpServer) {
        HttpServerRequestStream requestStream = httpServer.requestStream();
        requestStream.exceptionHandler(this::handleException);
        requestStream.endHandler(this::handleRequestEnd);
    }

    private Observable<HttpServerResponse> serveRequest(HttpServerRequest request, HttpServerResponse response, MultiMap params) {
        UserId userId = new UserId(getParam(params, "user"));
        CustomerId customerId = new CustomerId(getParam(params, "customerId"));

        return userService.getUser(userId).flatMap(user -> {
            Observable<JsonObject> customerObservable = customerService.getCustomerObservable(customerId).map(JsonObjects::toJson);
            Observable<JsonArray> accountObservable = accountService.getAccountsForCustomerObservable(customerId).map(JsonArray::new);
            Observable<JsonArray> creditCardObservable = creditCardService.getCreditCardsForCustomer(customerId).map(JsonObjects::toJsonArray);
            Observable<JsonArray> transactionObservable = transactionService.getTransactionsForCustomer(customerId).map(JsonObjects::toJsonArray);
            return zip(customerObservable, accountObservable, creditCardObservable, transactionObservable,
                    this::mergeIntoResponse);
        }).map(json -> writeResponse(response, json));
    }

    private JsonObject mergeIntoResponse(JsonObject cust, JsonArray accounts, JsonArray creditCards, JsonArray transactions) {
        return $()
                .put("customer", cust
                        .put("products", $()
                                .put("accounts", accounts)
                                .put("creditCards", creditCards))
                        .put("transactions", transactions))
                .put("branch", "empty")
                .put("appointments", "empty")
                .put("recommendations", "empty")
                .put("news", "empty")
                ;
    }

    private HttpServerResponse writeResponse(HttpServerResponse response, JsonObject responseJson) {
        Buffer content = new Buffer(io.vertx.core.buffer.Buffer.buffer(responseJson.encode()));
        response.headers().set("Content-Length", "" + content.length());
        response.headers().set("Access-Control-Allow-Origin", "*");
        return response.write(content);
    }

    private static String getParam(MultiMap params, String key) {
        return params.get(key);
    }

    private void handleRequestEnd(Void v) {
        logger.debug("request stream has been fully read");
    }

    private void handleException(Throwable throwable) {
        logger.error("error in request/response", throwable);
    }

    @Override
    public void stop() {
        logger.info("Verticle stopped");
    }


    private HttpServerOptions newServerConfig() {
        return new HttpServerOptions()
                .setHost(config().getString("host"))
                .setPort(config().getInteger("port"));
    }

    private void sendError(int statusCode, String statusMessage, HttpServerResponse response) {
        response.setStatusCode(statusCode).setStatusMessage(statusMessage);
    }
}
