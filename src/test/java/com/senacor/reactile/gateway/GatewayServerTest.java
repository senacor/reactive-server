package com.senacor.reactile.gateway;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.junit.Rule;
import org.junit.Test;

public class GatewayServerTest {

    private final Vertx vertx = Vertx.vertx();

    @Rule
    public final VertxRule vertxRule = new VertxRule(GatewayServer.class);

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