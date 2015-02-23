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

        vertx.deployVerticle("com.senacor.reactile.gateway.GatewayServer", response -> {
                    System.err.println("Start succeeded: " + response.succeeded());
                    HttpClient client = vertx.createHttpClient(new HttpClientOptions());
                    HttpClientRequest request = client.request(HttpMethod.GET, 8080, "localhost", "/the_uri");
                    request.toObservable().subscribe(
                            re -> {
                                System.out.println("response:" + re);
                            },
                            error -> {
                                System.out.println("error:" + error);
                                // Could not connect
                            }
                    );
                    request.end();
                }
        );


        System.err.println("Starting test");


    }

}