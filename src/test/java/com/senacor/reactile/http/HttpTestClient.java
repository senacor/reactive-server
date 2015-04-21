package com.senacor.reactile.http;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import static org.apache.commons.lang3.Validate.notNull;

public class HttpTestClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpTestClient.class);
    public static final long DEFAULT_TIMEOUT = 5000;
    private HttpClient client;

    public HttpTestClient(HttpClient client) {
        notNull(client);
        this.client = client;
    }

    public HttpTestClient(Vertx vertx, HttpClientOptions httpClientOptions) {
        this.client = vertx.createHttpClient(httpClientOptions.setDefaultPort(8081).setDefaultHost("localhost"));
    }

    public HttpTestClient(Vertx vertx) {
        this(vertx, new HttpClientOptions().setDefaultPort(8081).setDefaultHost("localhost"));
    }

    public HttpResponseStream getAsStream(String requestURI) throws Exception {
        CompletableFuture<HttpClientResponse> responseFuture = new CompletableFuture<>();
        CompletableFuture<String> bodyFuture = new CompletableFuture<>();
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        HttpClientRequest request = client.get(requestURI).setTimeout(DEFAULT_TIMEOUT);
        request.handler(response -> {
            responseFuture.complete(response);
            response.exceptionHandler(bodyFuture::completeExceptionally);
            response.handler(buffer -> {
                String data = buffer.getString(0, buffer.length());
                queue.add(data);
                bodyFuture.complete(data);
            });
        });
        request.exceptionHandler(responseFuture::completeExceptionally);
        request.end();

        return new HttpResponseImpl(responseFuture, bodyFuture, queue);
    }

    public HttpResponse get(String requestURI) throws Exception {
        CompletableFuture<HttpClientResponse> responseFuture = new CompletableFuture<>();
        CompletableFuture<String> bodyFuture = new CompletableFuture<>();
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        HttpClientRequest request = client.get(requestURI).setTimeout(DEFAULT_TIMEOUT);
        request.handler(response -> {
            responseFuture.complete(response);
            response.bodyHandler(buffer -> bodyFuture.complete(buffer.getString(0, buffer.length())));
            response.exceptionHandler(bodyFuture::completeExceptionally);
        });
        request.exceptionHandler(responseFuture::completeExceptionally);
        request.end();

        return new HttpResponseImpl(responseFuture, bodyFuture, queue);
    }

    public HttpResponse put(String requestURI, Jsonizable document) throws Exception {
        return put(requestURI, document.toJson());
    }

    public HttpResponse put(String requestURI, JsonObject document) throws Exception {
        CompletableFuture<HttpClientResponse> responseFuture = new CompletableFuture<>();
        CompletableFuture<String> bodyFuture = new CompletableFuture<>();
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        HttpClientRequest request = client.put(requestURI, (resp) -> {
        }).setTimeout(DEFAULT_TIMEOUT);

        request.handler(response -> {
            responseFuture.complete(response);
            response.bodyHandler(buffer -> bodyFuture.complete(buffer.getString(0, buffer.length())));
            response.exceptionHandler(bodyFuture::completeExceptionally);
        });
        request.exceptionHandler(responseFuture::completeExceptionally);
        request.putHeader("content-type", "application/json");
        request.end(document.encode());

        return new HttpResponseImpl(responseFuture, bodyFuture, queue);
    }

    public HttpResponse post(String requestURI, Jsonizable document) throws Exception {
        return post(requestURI, document.toJson());
    }

    public HttpResponse post(String requestURI, JsonObject document) throws Exception {
        CompletableFuture<HttpClientResponse> responseFuture = new CompletableFuture<>();
        CompletableFuture<String> bodyFuture = new CompletableFuture<>();
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        HttpClientRequest request = client.post(requestURI).setTimeout(DEFAULT_TIMEOUT);

        request.handler(response -> {
            responseFuture.complete(response);
            response.bodyHandler(buffer -> bodyFuture.complete(buffer.getString(0, buffer.length())));
            response.exceptionHandler(bodyFuture::completeExceptionally);//response.handler(buffer -> queue.add(buffer.getString(0, buffer.length())));
        });
        request.exceptionHandler(responseFuture::completeExceptionally);
        request.putHeader("content-type", "application/json");
        request.end(document.encode());

        return new HttpResponseImpl(responseFuture, bodyFuture, queue);
    }
}
