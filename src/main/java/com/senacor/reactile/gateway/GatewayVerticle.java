package com.senacor.reactile.gateway;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.senacor.reactile.gateway.commands.AppointmentFindByBranchCommandFactory;
import com.senacor.reactile.gateway.commands.AppointmentFindByCustomerCommandFactory;
import com.senacor.reactile.gateway.commands.BranchesReadCommandFactory;
import com.senacor.reactile.gateway.commands.CustomerUpdateAddressCommandFactory;
import com.senacor.reactile.gateway.commands.StartCommandFactory;
import com.senacor.reactile.gateway.commands.UserFindCommandFactory;
import com.senacor.reactile.gateway.commands.UserReadCommandFactory;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.appointment.AppointmentService;
import com.senacor.reactile.service.branch.BranchService;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.newsticker.NewsService;
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

public class GatewayVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(GatewayVerticle.class);
    public static final int DEFAULT_NUMBER_OF_NEWS_ITEMS = 15;

    private final CustomerUpdateAddressCommandFactory customerUpdateAddressCommandFactory;
    private final UserReadCommandFactory userReadCommandFactory;
    private final UserFindCommandFactory userFindCommandFactory;
    private final AppointmentService appointmentService;
    private final AppointmentFindByCustomerCommandFactory appointmentFindByCustomerCommandFactory;
    private final AppointmentFindByBranchCommandFactory appointmentFindByBranchCommandFactory;
    private final BranchesReadCommandFactory branchesReadCommandFactory;
    private final BranchService branchService;
    private final NewsService newsService;
    private final StartCommandFactory startCommandFactory;

    @Inject
    public GatewayVerticle(CustomerUpdateAddressCommandFactory customerUpdateAddressCommandFactory, StartCommandFactory startCommandFactory,
        UserReadCommandFactory userReadCommandFactory, UserFindCommandFactory userFindCommandFactory, AppointmentService appointmentService,
        AppointmentFindByCustomerCommandFactory appointmentFindByCustomerCommandFactory,
        AppointmentFindByBranchCommandFactory appointmentFindByBranchCommandFactory, BranchesReadCommandFactory branchesReadCommandFactory,
        BranchService branchService, NewsService newsService) {
        this.customerUpdateAddressCommandFactory = customerUpdateAddressCommandFactory;
        this.startCommandFactory = startCommandFactory;
        this.userReadCommandFactory = userReadCommandFactory;
        this.userFindCommandFactory = userFindCommandFactory;
        this.appointmentService = appointmentService;
        this.appointmentFindByCustomerCommandFactory = appointmentFindByCustomerCommandFactory;
        this.appointmentFindByBranchCommandFactory = appointmentFindByBranchCommandFactory;
        this.branchesReadCommandFactory = branchesReadCommandFactory;
        this.branchService = branchService;
        this.newsService = newsService;
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
            .addOutboundPermitted(new PermittedOptions().setAddressRegex(PushNotificationVerticle.PUBLISH_ADDRESS_APPOINTMENT_UPDATE + ".*"))
            .addOutboundPermitted(new PermittedOptions().setAddressRegex(PushNotificationVerticle.PUBLISH_ADDRESS_APPOINTMENT_DELETE + ".*"))
            .addOutboundPermitted(new PermittedOptions().setAddressRegex(PushNotificationVerticle.PUBLISH_ADDRESS_NEWS_UPDATED));
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(bridgeOptions));

        // common handler:
        router.route().handler(TimeoutHandler.create(config().getLong("timeout")));
        router.route().handler(ResponseTimeHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(this::contentTypeJson);

        router.route("/customer/:customerId/addresses").method(HttpMethod.POST).method(HttpMethod.PUT).handler(this::handleUpdateAddress);
        router.get("/start").handler(this::handleStart);

        router.get("/users/:userId").method(HttpMethod.GET).handler(this::handleGetUser);
        router.get("/users/").method(HttpMethod.GET).handler(this::handleFindUser);

        router.get("/appointments/customer/:customerId").method(HttpMethod.GET).handler(this::handleGetCustomerAppointments);
        router.get("/appointments/branch/:branchId").method(HttpMethod.GET).handler(this::handleGetBranchAppointments);
        router.get("/appointments/create/").method(HttpMethod.POST).method(HttpMethod.PUT).handler(this::handleCreateAppointment);
        router.get("/appointments/update/:appointmentId").method(HttpMethod.POST).method(HttpMethod.PUT).handler(this::handleUpdateAppointment);
        router.get("/appointments/delete/:appointmentId").method(HttpMethod.POST).method(HttpMethod.PUT).method(HttpMethod.GET)
            .handler(this::handleDeleteAppointment);
        router.get("/appointments/").method(HttpMethod.GET).handler(this::handleGetAllAppointments);

        router.get("/branches/").method(HttpMethod.GET).handler(this::handleGetBranches);
        router.get("/branch/:branchId").method(HttpMethod.GET).handler(this::handleFindBranch);

        router.get("/news/").method(HttpMethod.GET).handler(this::getLatestNewsItems);
        router.get("/news/:max").method(HttpMethod.GET).handler(this::getLatestNewsItems);

        // common handler:
        router.route().handler(this::end);
        // StaticHandler don't call RoutingContext.next()
        router.route().handler(StaticHandler.create());

        HttpServerOptions serverOptions = newServerConfig();
        HttpServer httpServer = vertx.createHttpServer(serverOptions);
        httpServer.requestHandler(router::accept).listenObservable()
            .subscribe(server -> logger.info("Router Listening at " + serverOptions.getHost() + ":" + serverOptions.getPort()),
                failure -> logger.error("Router Failed to start", failure));
    }

    private void handleFindUser(RoutingContext routingContext) {
        MultiMap params = routingContext.request().params();
        Map<String, String> map = new HashMap<>();
        for (String p : params.names()) {
            map.put(p, params.get(p));
        }
        HttpServerResponse resp = routingContext.response();
        userFindCommandFactory.create(map).toObservable().map(users -> writeResponse(resp, new JsonObject().put("users", users)))
            .subscribe(res -> routingContext.next());
    }

    private void handleGetUser(RoutingContext routingContext) {
        String userIdStr = routingContext.request().getParam("userId");
        HttpServerResponse resp = routingContext.response();

        if (userIdStr == null) {
            logger.warn("Request Param :userId is null");
            sendError(400, "missing userId parameter", routingContext);
        } else {
            UserId userId = new UserId(userIdStr);
            userReadCommandFactory.create(userId).toObservable().map(user -> writeResponse(resp, user.toJson()))
                .subscribe(res -> routingContext.next());
        }
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
                    .map(customer -> writeResponse(response, customer.toJson())).subscribe(res -> routingContext.next());
            }
        }
    }

    private void handleGetAllAppointments(RoutingContext routingContext) {
        HttpServerResponse resp = routingContext.response();

        appointmentService.getAllAppointments().map(appointments -> writeResponse(resp, new JsonObject().put("appointments", appointments.toJson())))
            .subscribe(res -> routingContext.next());
    }

    private void handleGetCustomerAppointments(RoutingContext routingContext) {
        String customerIdStr = routingContext.request().getParam("customerId");

        if (customerIdStr == null) {
            logger.warn("Request Param :customerId is null");
            sendError(400, "missing customerId parameter", routingContext);
        } else {
            HttpServerResponse resp = routingContext.response();
            CustomerId customerId = new CustomerId(customerIdStr);
            appointmentFindByCustomerCommandFactory.create(customerId).toObservable()
                .map(appointments -> writeResponse(resp, new JsonObject().put("appointmentsByCustomer", appointments)))
                .subscribe(res -> routingContext.next());
        }
    }

    private void handleGetBranchAppointments(RoutingContext routingContext) {
        String branchId = routingContext.request().getParam("branchId");

        if (branchId == null) {
            logger.warn("Request Param :branchId is null");
            sendError(400, "missing branchId parameter", routingContext);
        } else {
            HttpServerResponse resp = routingContext.response();
            appointmentFindByBranchCommandFactory.create(branchId).toObservable()
                .map(appointments -> writeResponse(resp, new JsonObject().put("appointmentsByBranch", appointments)))
                .subscribe(res -> routingContext.next());
        }
    }

    private void handleCreateAppointment(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();

        JsonObject newAppointmentJson = routingContext.getBodyAsJson();
        if (newAppointmentJson == null) {
            logger.warn("body is null");
            sendError(400, "body is null", routingContext);
        } else {
            Appointment newAppointment = Appointment.fromJson(newAppointmentJson);
            appointmentService.createOrUpdateAppointment(newAppointment).map(appointment -> writeResponse(response, appointment.toJson()))
                .subscribe(res -> routingContext.next());
        }
    }

    private void handleUpdateAppointment(RoutingContext routingContext) {
        String appointmentIdStr = routingContext.request().getParam("appointmentId");
        HttpServerResponse response = routingContext.response();
        if (appointmentIdStr == null) {
            logger.warn("Request Param :appointmentId is null");
            sendError(400, "missing appointment parameter", routingContext);
        } else {
            JsonObject newAppointmentJson = routingContext.getBodyAsJson();
            if (newAppointmentJson == null) {
                logger.warn("body is null");
                sendError(400, "body is null", routingContext);
            } else {
                Appointment newAppointment = Appointment.fromJson(newAppointmentJson);
                appointmentService.createOrUpdateAppointment(newAppointment).map(appointment -> writeResponse(response, appointment.toJson()))
                    .subscribe(res -> routingContext.next());
            }
        }
    }

    private void handleDeleteAppointment(RoutingContext routingContext) {
        String appointmentIdStr = routingContext.request().getParam("appointmentId");
        HttpServerResponse response = routingContext.response();
        if (appointmentIdStr == null) {
            logger.warn("Request Param :appointmentId is null");
            sendError(400, "missing appointment parameter", routingContext);
        } else {
            appointmentService.deleteAppointment(appointmentIdStr).map(appointment -> writeResponse(response, appointment.toJson()))
                .subscribe(res -> routingContext.next());
        }
    }

    private void handleGetBranches(RoutingContext routingContext) {
        HttpServerResponse resp = routingContext.response();
        branchesReadCommandFactory.create().toObservable().map(branches -> writeResponse(resp, new JsonObject().put("branches", branches)))
            .subscribe(res -> routingContext.next());
    }

    private void handleFindBranch(RoutingContext routingContext) {
        String branchId = routingContext.request().getParam("branchId");

        if (branchId == null) {
            logger.warn("Request Param :branchId is null");
            sendError(400, "missing branchId parameter", routingContext);
        } else {
            HttpServerResponse resp = routingContext.response();
            branchService.getBranch(branchId).map(branch -> writeResponse(resp, branch.toJson())).subscribe(res -> routingContext.next());
        }
    }

    private void getLatestNewsItems(RoutingContext routingContext) {
        String maxNewsItems = routingContext.request().getParam("max");
        int max;
        if (maxNewsItems == null) {
            max = DEFAULT_NUMBER_OF_NEWS_ITEMS;
        } else {
            max = Integer.parseInt(maxNewsItems);
        }

        HttpServerResponse resp = routingContext.response();

        newsService.getLatestNews(max).map(newsItems -> writeResponse(resp, new JsonObject().put("latestNews", newsItems.toJson())))
            .subscribe(res -> routingContext.next());
    }

    private void handleStart(RoutingContext routingContext) {
        serveRequest(routingContext.response(), routingContext.request().params())
            .subscribe(response -> routingContext.next(), Throwable::printStackTrace);
    }

    private Observable<HttpServerResponse> serveRequest(HttpServerResponse response, MultiMap params) {
        UserId userId = new UserId(getParam(params, "user"));
        CustomerId customerId = new CustomerId(getParam(params, "customerId"));

        return startCommandFactory.create(userId, customerId).toObservable().map(json -> writeResponse(response, json));
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
        return new HttpServerOptions().setHost(config().getString("host")).setPort(config().getInteger("port"));
    }

    private void sendError(int statusCode, String statusMessage, RoutingContext context) {
        context.response().setStatusCode(statusCode).setStatusMessage(statusMessage);
        context.response().end();
    }
}
