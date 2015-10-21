package com.senacor.reactile.hystrix.metrics.eventstream;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsPoller;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;

import javax.inject.Inject;

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

    private final Logger logger = LoggerFactory.getLogger(HystrixMetricsStreamVerticle.class);
    private HttpServer server;

    private final MetricsBridge metricsBridge;

    @Inject
    public HystrixMetricsStreamVerticle(MetricsBridge metricsBridge) {
        this.metricsBridge = metricsBridge;
    }

    @Override
    public void start() {
        HttpServerOptions serverOptions = newServerConfig();
        server = vertx.createHttpServer(serverOptions);
        int defaultDelay = config().getInteger("delay", 5000);

        server.requestHandler(request -> {
            if ("/hystrix.stream".equals(request.path())) {
                String delay = request.getParam("delay");
                metricsBridge.stream(request, null != delay ? Integer.parseInt(delay) : defaultDelay);
            } else {
                request.response().setStatusCode(404).setStatusMessage("not found").end();
            }
        }).listenObservable()
                .subscribe(httpServer -> logger.info("HystrixMetricsStreamVerticle Listening at " + serverOptions.getHost() + ":" + serverOptions.getPort()),
                        failure -> logger.error("HystrixMetricsStreamVerticle Failed to start", failure));
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        server.closeObservable()
                .doOnCompleted(stopFuture::complete)
                .doOnError(stopFuture::fail)
                .subscribe();
    }

    private HttpServerOptions newServerConfig() {
        return new HttpServerOptions()
                .setHost(config().getString("host"))
                .setPort(config().getInteger("port"));
    }
}
