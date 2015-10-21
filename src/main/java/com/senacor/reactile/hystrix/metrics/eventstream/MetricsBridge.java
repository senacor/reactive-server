package com.senacor.reactile.hystrix.metrics.eventstream;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsPoller;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;

public class MetricsBridge {
    private final Logger logger = LoggerFactory.getLogger(MetricsBridge.class);

    void stream(HttpServerRequest request, int delay) {
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

}
