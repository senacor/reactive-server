package com.senacor.reactile.http;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;

import java.util.concurrent.CompletableFuture;

public class HttpTestClient {
    public static final long DEFAULT_TIMEOUT = 1500;
    private HttpClient client;

    public HttpTestClient(Vertx vertx, HttpClientOptions httpClientOptions) {
        this.client = vertx.createHttpClient(httpClientOptions.setDefaultPort(8081).setDefaultHost("localhost"));
    }

    public HttpTestClient(Vertx vertx) {
        this(vertx, new HttpClientOptions().setDefaultPort(8081).setDefaultHost("localhost"));
    }

    public HttpResponse get(String requestURI) throws Exception {
        CompletableFuture<HttpClientResponse> responseFuture = new CompletableFuture<>();
        CompletableFuture<String> bodyFuture = new CompletableFuture<>();
        HttpClientRequest request = client.get(requestURI).setTimeout(DEFAULT_TIMEOUT);
        request.handler(response -> {
            responseFuture.complete(response);
            response.bodyHandler(buffer -> bodyFuture.complete(buffer.getString(0, buffer.length())));
            response.exceptionHandler(bodyFuture::completeExceptionally);
        });
        request.exceptionHandler(responseFuture::completeExceptionally);
        request.end();

        return new HttpResponseImpl(responseFuture, bodyFuture);
    }
}
