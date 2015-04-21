package com.senacor.reactile.http;

import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpClientResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class HttpResponseImpl implements HttpResponse, HttpResponseStream {

    public static final long DEFAULT_TIMEOUT = 1500;

    private final CompletableFuture<HttpClientResponse> responseFuture;
    private final CompletableFuture<String> bodyFuture;
    private final LinkedBlockingQueue<String> dataQueue;

    public HttpResponseImpl(CompletableFuture<HttpClientResponse> responseFuture,
                            CompletableFuture<String> bodyFuture,
                            LinkedBlockingQueue<String> dataQueue) {
        this.responseFuture = responseFuture;
        this.bodyFuture = bodyFuture;
        this.dataQueue = dataQueue;
    }

    @Override
    public HttpClientResponse getHttpClientResponse() {
        return get(responseFuture);
    }

    @Override
    public int statusCode() {
        return get(responseFuture).statusCode();
    }

    @Override
    public String statusMessage() {
        return get(responseFuture).statusMessage();
    }

    @Override
    public MultiMap headers() {
        return get(responseFuture).headers();
    }

    @Override
    public String getBody() {
        return get(bodyFuture);
    }

    @Override
    public String getNextData() {
        try {
            return dataQueue.poll(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private static <T> T get(CompletableFuture<T> future) {
        try {
            return future.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
