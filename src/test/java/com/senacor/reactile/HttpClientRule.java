package com.senacor.reactile;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.junit.rules.ExternalResource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HttpClientRule extends ExternalResource {
    public static final int DEFAULT_TIMEOUT = 500;
    private final Vertx vertx;
    private final HttpClientOptions httpClientOptions;
    private HttpClient client;

    public HttpClientRule(Vertx vertx, HttpClientOptions httpClientOptions) {
        this.vertx = vertx;
        this.httpClientOptions = httpClientOptions;
    }

    public HttpClientRule(Vertx vertx) {
        this(vertx, new HttpClientOptions());
    }

    @Override
    protected void before() throws Throwable {
        this.client = vertx.createHttpClient(new HttpClientOptions());

    }

    public HttpClientRequest request(HttpMethod method, String requestURI) {
        return request(method, 8080, "localhost", requestURI);

    }

    public HttpClientRequest request(HttpMethod method, int port, String host, String requestURI) {
        return client.request(method, port, host, requestURI);
    }

    public String readBody(HttpClientRequest request) {
        CompletableFuture<String> bodyFuture = new CompletableFuture<>();
        request.toObservable()
                .flatMap(response -> response.toObservable())
                .map(buffer -> buffer.getString(0, buffer.length()))
                .subscribe(
                        bodyFuture::complete,
                        bodyFuture::completeExceptionally
                );
        request.end();
        try {
            return bodyFuture.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
