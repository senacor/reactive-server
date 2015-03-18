package com.senacor.reactile.http;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpClientResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HttpResponseImpl implements HttpResponse {

    public static final long DEFAULT_TIMEOUT = 1500;


    private final CompletableFuture<HttpClientResponse> responseFuture;
    private final CompletableFuture<String> bodyFuture;

    public HttpResponseImpl(CompletableFuture<HttpClientResponse> responseFuture, CompletableFuture<String> bodyFuture) {
        this.responseFuture = responseFuture;
        this.bodyFuture = bodyFuture;
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
    public JsonObject asJson() {
        return new JsonObject(getBody());
    }

    private static <T> T get(CompletableFuture<T> future) {
        try {
            return future.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
