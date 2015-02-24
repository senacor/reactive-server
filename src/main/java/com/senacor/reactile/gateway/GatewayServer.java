package com.senacor.reactile.gateway;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;

public class GatewayServer extends AbstractVerticle {

    public void start() {
        HttpServerOptions options = new HttpServerOptions().setPort(8080).setHost("localhost");
        HttpServer httpServer = vertx.createHttpServer(options);
        httpServer.requestStream().toObservable().subscribe(request -> request.response().end("huhu"));

        httpServer.listenObservable().subscribe(
                server -> System.out.println("Listening at " + options.getHost() + ":" + options.getPort()),
                failure -> System.err.println("Failed to start")
        );
    }

    public void stop() {
        System.out.println("Verticle stopped");
    }

}
