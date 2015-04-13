package com.senacor.reactile.http;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.*;

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

    public HttpResponse put(String requestURI, Jsonizable document) throws Exception {
        return put(requestURI, document.toJson());
    }

    public HttpResponse put(String requestURI, JsonObject document) throws Exception {
        CompletableFuture<HttpClientResponse> responseFuture = new CompletableFuture<>();
        CompletableFuture<String> bodyFuture = new CompletableFuture<>();
        HttpClientRequest request = client.put(requestURI, (resp) -> {}).setTimeout(DEFAULT_TIMEOUT);

        request.handler(response -> {
            responseFuture.complete(response);
            response.bodyHandler(buffer -> bodyFuture.complete(buffer.getString(0, buffer.length())));
            response.exceptionHandler(bodyFuture::completeExceptionally);
        });
        request.exceptionHandler(responseFuture::completeExceptionally);
        request.putHeader("content-type", "application/json");
        request.end(document.encode());

        return new HttpResponseImpl(responseFuture, bodyFuture);
    }

    public HttpResponse post(String requestURI, Jsonizable document) throws Exception {
        return post(requestURI, document.toJson());
    }

    public HttpResponse post(String requestURI, JsonObject document) throws Exception {
        CompletableFuture<HttpClientResponse> responseFuture = new CompletableFuture<>();
        CompletableFuture<String> bodyFuture = new CompletableFuture<>();
        HttpClientRequest request = client.post(requestURI).setTimeout(DEFAULT_TIMEOUT);

        request.handler(response -> {
            responseFuture.complete(response);
            response.bodyHandler(buffer -> bodyFuture.complete(buffer.getString(0, buffer.length())));
            response.exceptionHandler(bodyFuture::completeExceptionally);
        });
        request.exceptionHandler(responseFuture::completeExceptionally);
        request.putHeader("content-type", "application/json");
        request.end(document.encode());

        return new HttpResponseImpl(responseFuture, bodyFuture);
    }
}
