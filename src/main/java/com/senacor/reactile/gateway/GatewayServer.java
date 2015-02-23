package com.senacor.reactile.gateway;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.AbstractVerticle;

public class GatewayServer extends AbstractVerticle {

    public void start() {
        HttpServerOptions options = new HttpServerOptions().setPort(8080).setHost("localhost");
        vertx.createHttpServer(options).requestHandler(request -> {
            request.response().end("huhu");
        }).listenObservable().subscribe(
                server -> {
                    System.out.println("Listening at " + options.getHost() + ":" + options.getPort());
                },
                failure -> {
                    System.err.println("Failed to start");
                }
        );
    }

    public void stop() {
        System.out.println("Verticle stopped");
    }

}
