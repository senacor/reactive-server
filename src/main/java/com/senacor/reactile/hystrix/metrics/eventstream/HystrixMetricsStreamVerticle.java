package com.senacor.reactile.hystrix.metrics.eventstream;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsPoller;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;

/**
 * Verticle which streams Hystrix Metrics
 * <p>
 * Test with: curl http://localhost:8082/hystrix.stream
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 16.04.15
 * Time: 17:23
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class HystrixMetricsStreamVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(HystrixMetricsStreamVerticle.class);
    private HttpServer server;

    @Override
    public void start() {
        HttpServerOptions serverOptions = newServerConfig();
        server = vertx.createHttpServer(serverOptions);
        int defaultDelay = config().getInteger("delay", 5000);

        server.requestHandler(request -> {
            if ("/hystrix.stream".equals(request.path())) {
                String delay = request.getParam("delay");
                stream(request, null != delay ? Integer.parseInt(delay) : defaultDelay);
            } else {
                request.response().setStatusCode(404).setStatusMessage("not found").end();
            }
        }).listenObservable()
                .subscribe(httpServer -> logger.info("HystrixMetricsStreamVerticle Listening at " + serverOptions.getHost() + ":" + serverOptions.getPort()),
                        failure -> logger.error("HystrixMetricsStreamVerticle Failed to start", failure));
    }

    @Override
    public void stop() throws Exception {
        server.close();
    }

    private void stream(HttpServerRequest request, int delay) {
        final HttpServerResponse response = request.response();

        response.setChunked(true);

        response.headers().add("Content-Type", "text/event-stream;charset=UTF-8");
        response.headers().add("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.headers().add("Pragma", "no-cache");

        HystrixMetricsPoller poller = new HystrixMetricsPoller(json -> response.write("data: " + json + "\n\n"), delay);
        logger.info("Starting poller with delay=" + delay + "ms");
        poller.start();
        response.closeHandler(res -> {
            logger.info("closeHandler: Shutting down poller");
            poller.shutdown();
        });
        response.exceptionHandler(res -> {
            logger.info("exceptionHandler: Shutting down poller");
            poller.shutdown();
        });
    }

    private HttpServerOptions newServerConfig() {
        return new HttpServerOptions()
                .setHost(config().getString("host"))
                .setPort(config().getInteger("port"));
    }
}
