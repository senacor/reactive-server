package com.senacor.reactile.gateway;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;

public class GatewayServer extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public void start() {
        HttpServerOptions options = new HttpServerOptions().setPort(8080).setHost("localhost");
        HttpServer httpServer = vertx.createHttpServer(options);
        httpServer.requestStream().toObservable().subscribe(request -> request.response().end("huhu"));

        httpServer.listenObservable().subscribe(
                server -> log.info("Listening at " + options.getHost() + ":" + options.getPort()),
                failure -> log.error("Failed to start")
        );
    }

    @Override
    public void stop() {
        log.info("Verticle stopped");
    }

}
