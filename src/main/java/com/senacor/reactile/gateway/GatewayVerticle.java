package com.senacor.reactile.gateway;

import com.senacor.reactile.gateway.commands.BranchOverviewCommandFactory;
import com.senacor.reactile.gateway.commands.AppointmentsSummaryCommandFactory;
import com.senacor.reactile.gateway.commands.CustomerUpdateAddressCommandFactory;
import com.senacor.reactile.gateway.commands.GetAppointmentCommandFactory;
import com.senacor.reactile.gateway.commands.StartCommandFactory;
import com.senacor.reactile.gateway.commands.UserReadCommandFactory;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.user.UserId;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.handler.sockjs.BridgeOptions;
import io.vertx.ext.apex.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.apex.Router;
import io.vertx.rxjava.ext.apex.RoutingContext;
import io.vertx.rxjava.ext.apex.handler.BodyHandler;
import io.vertx.rxjava.ext.apex.handler.ResponseTimeHandler;
import io.vertx.rxjava.ext.apex.handler.StaticHandler;
import io.vertx.rxjava.ext.apex.handler.TimeoutHandler;
import io.vertx.rxjava.ext.apex.handler.sockjs.SockJSHandler;
import rx.Observable;

import javax.inject.Inject;

public class GatewayVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(GatewayVerticle.class);

    private final CustomerUpdateAddressCommandFactory customerUpdateAddressCommandFactory;
    private final UserReadCommandFactory userReadCommandFactory;
    private final StartCommandFactory startCommandFactory;
    private final GetAppointmentCommandFactory getAppointmentCommandFactory;
    private final BranchOverviewCommandFactory branchCommandFactory;
    private final AppointmentsSummaryCommandFactory appointmentsSummaryCommandFactory;

    @Inject
    public GatewayVerticle(
            CustomerUpdateAddressCommandFactory customerUpdateAddressCommandFactory,
            StartCommandFactory startCommandFactory,
            BranchOverviewCommandFactory branchCommandFactory,
            UserReadCommandFactory userReadCommandFactory,
            GetAppointmentCommandFactory getAppointmentCommandFactory,
            AppointmentsSummaryCommandFactory appointmentsSummaryCommandFactory) {
        this.customerUpdateAddressCommandFactory = customerUpdateAddressCommandFactory;
        this.startCommandFactory = startCommandFactory;
        this.userReadCommandFactory = userReadCommandFactory;
        this.appointmentsSummaryCommandFactory = appointmentsSummaryCommandFactory;
        this.getAppointmentCommandFactory = getAppointmentCommandFactory;
        this.branchCommandFactory = branchCommandFactory;
    }

    @Override
    public void start() {
        createRxRouter();
    }

    private void createRxRouter() {
        Router router = Router.router(vertx);
        router.exceptionHandler(this::handleException);

        // Export Eventbus
        BridgeOptions bridgeOptions = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddressRegex(PushNotificationVerticle.PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE + ".*"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex(PushNotificationVerticle.PUBLISH_ADDRESS_BRANCH_ADDRESS_UPDATE + ".*"));
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(bridgeOptions));

        // common handler:
        router.route().handler(TimeoutHandler.create(config().getLong("timeout")));
        router.route().handler(ResponseTimeHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(this::contentTypeJson);

        router.route("/customer/:customerId/addresses")
                .method(HttpMethod.POST).method(HttpMethod.PUT)
                .handler(this::handleUpdateAddress);
        router.get("/start").handler(this::handleStart);

        router.get("/users/:userId").method(HttpMethod.GET).handler(this::handleGetUser);

        router.get("/appointment/:appointmentId").handler(this::handleGetAppointment);
        router.get("/appointments-summary").method(HttpMethod.GET).handler(this::handleGetAppointmentSummary);

        // branch
        router.route("/branches/:branchId/overview")
              .method(HttpMethod.GET)
              .handler(this::handleBranchOverview);

        // branch
        router.route("/branches/:branchId/overview")
              .method(HttpMethod.GET)
              .handler(this::handleBranchOverview);

        // common handler:
        router.route().handler(this::end);
        // StaticHandler don't call RoutingContext.next()
        router.route().handler(StaticHandler.create());

        HttpServerOptions serverOptions = newServerConfig();
        HttpServer httpServer = vertx.createHttpServer(serverOptions);
        httpServer.requestHandler(router::accept)
                .listenObservable()
                .subscribe(server -> logger.info("Router Listening at " + serverOptions.getHost() + ":" + serverOptions.getPort()),
                        failure -> logger.error("Router Failed to start", failure));
    }

    private void handleGetUser(RoutingContext routingContext){
        String userIdStr = routingContext.request().getParam("userId");
        HttpServerResponse resp = routingContext.response();

        if (userIdStr == null){
            logger.warn("Request Param :userId is null");
            sendError(400, "missing userId parameter", routingContext);
        } else {
            UserId userId = new UserId(userIdStr);
            userReadCommandFactory.create(userId).toObservable().map(user -> writeResponse(resp, user.toJson()))
            .subscribe(res -> routingContext.next());
        }
    }

    private void handleGetAppointmentSummary(RoutingContext routingContext){
        HttpServerResponse resp = routingContext.response();
        appointmentsSummaryCommandFactory.create().toObservable().map(json -> writeResponse(resp, json))
                .subscribe(res -> routingContext.next());
    }

    private void handleUpdateAddress(RoutingContext routingContext) {
        String customerIdString = routingContext.request().getParam("customerId");
        HttpServerResponse response = routingContext.response();
        if (customerIdString == null) {
            logger.warn("Request Param :customerId is null");
            sendError(400, "missing customerId parameter", routingContext);
        } else {
            CustomerId customerId = new CustomerId(customerIdString);
            //logger.info("body: " + routingContext.getBodyAsString());
            JsonObject newAddressJson = routingContext.getBodyAsJson();
            if (newAddressJson == null) {
                logger.warn("body is null");
                sendError(400, "body is null", routingContext);
            } else {
                Address newAddress = Address.fromJson(newAddressJson);
                customerUpdateAddressCommandFactory.create(customerId, newAddress).toObservable()
                        .map(customer -> writeResponse(response, customer.toJson()))
                        .subscribe(res -> routingContext.next());
            }
        }
    }

    private void handleGetAppointment(RoutingContext routingContext) {
        String appointmentIdString = routingContext.request().getParam("appointmentId");
        HttpServerResponse response = routingContext.response();

        if (appointmentIdString == null) {
            logger.warn("Request Param :appointmentId is null");
            sendError(400, "missing appointmentId parameter", routingContext);
        } else {
            getAppointmentCommandFactory.create(appointmentIdString).toObservable()
                    .map(appointment -> writeResponse(response, appointment)).subscribe(res -> routingContext.next());
        }
    }

    private void handleStart(RoutingContext routingContext) {
        serveRequest(routingContext.response(), routingContext.request().params())
                .subscribe(
                        response -> routingContext.next(),
                        Throwable::printStackTrace);
    }

    private Observable<HttpServerResponse> serveRequest(HttpServerResponse response, MultiMap params) {
        UserId userId = new UserId(getParam(params, "user"));
        CustomerId customerId = new CustomerId(getParam(params, "customerId"));

        return startCommandFactory.create(userId, customerId).toObservable()
                .map(json -> writeResponse(response, json));
    }

    private void handleBranchOverview(RoutingContext routingContext) {
        serveBranchOverviewRequest(routingContext.response(), routingContext.request().params())
                .subscribe(
                        response -> routingContext.next(),
                        Throwable::printStackTrace);
    }

    private Observable<HttpServerResponse> serveBranchOverviewRequest(HttpServerResponse response, MultiMap params) {
        String branchId = getParam(params, "branchId");

        return branchCommandFactory.overview(branchId).toObservable()
                                  .map(json -> writeResponse(response, json));
    }

    private void contentTypeJson(RoutingContext context) {
        context.response().putHeader("content-type", "application/json");
        context.next();
    }

    private void end(RoutingContext context) {
        context.response().end();
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

    private void sendError(int statusCode, String statusMessage, RoutingContext context) {
        context.response().setStatusCode(statusCode).setStatusMessage(statusMessage);
        context.response().end();
    }
}
