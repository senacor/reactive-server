package com.senacor.reactile.gateway;

import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.customer.CustomerService;
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

public class GatewayServer extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JsonMarshaller jsonMarshaller = new JsonMarshaller();

    @Override
    public void start() {
        HttpServerOptions options = newServerConfig();
        HttpServer httpServer = vertx.createHttpServer(options);

        HttpServerRequestStream requestStream = httpServer.requestStream();
        requestStream.exceptionHandler(this::handleException);
        requestStream.endHandler(this::handleRequestEnd);
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

    private Observable<HttpServerResponse> handleRequest(HttpServerRequest request) {
        return serveRequest(request, request.response(), request.params());
    }

    private Observable<HttpServerResponse> serveRequest(HttpServerRequest request, HttpServerResponse response, MultiMap params) {
        return getCustomer(getParam(params, "customerId"))
                .map(customer -> {
                    Buffer content = jsonMarshaller.toBuffer(customer);
                    response.headers().set("Content-Length", "" + content.length());
                    return response.write(content);
                });
    }

    private Observable<Customer> getCustomer(String customerId) {
        Observable<Message<Customer>> messageObservable = vertx.eventBus().sendObservable(CustomerService.ADDRESS, new CustomerId(customerId));
        return messageObservable.map(message -> message.body());
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
