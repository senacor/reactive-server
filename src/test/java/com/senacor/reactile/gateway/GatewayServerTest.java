package com.senacor.reactile.gateway;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GatewayServerTest {

    private final Vertx vertx = Vertx.vertx();

    @Before
    public void init() {
        vertx.deployVerticle("com.senacor.reactile.gateway.GatewayServer", response -> {
                    System.err.println("Start succeeded: " + response.succeeded());
                }
        );
    }

    @After
    public void stop() {
        vertx.undeployVerticle("com.senacor.reactile.gateway.GatewayServer", response -> {
                    System.out.println("Stop succeeded: " + response.succeeded());
                }
        );
    }

    @Test
    public void thatRequestsAreHandled() throws InterruptedException {
        HttpClient client = vertx.createHttpClient(new HttpClientOptions());
        Thread.sleep(2000);
        HttpClientRequest request = client.request(HttpMethod.GET, 8080, "localhost", "/the_uri");
        request.toObservable().subscribe(
                response -> {
                    response.bodyHandler(handler -> {
                                System.out.println("response: " + handler.getString(0, handler.length()));
                            }
                    );
                },
                error -> {
                    System.out.println("error:" + error);
                    // Could not connect
                }
        );
        request.end();
        Thread.sleep(2000);

    }

}