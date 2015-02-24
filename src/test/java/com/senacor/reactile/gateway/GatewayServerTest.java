package com.senacor.reactile.gateway;

import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.CustomerService;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class GatewayServerTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(GatewayServer.class, CustomerService.class);

    @Test(timeout = 1000)
    public void thatRequestsAreHandled() throws InterruptedException {
        HttpClient client = vertxRule.vertx().createHttpClient(new HttpClientOptions());
        HttpClientRequest request = client.request(HttpMethod.GET, 8080, "localhost", "/start?user=momann&customerId=007");

        CompletableFuture<Object> responseFuture = new CompletableFuture<>();
        request.toObservable().subscribe(
                response -> response.bodyHandler(handler -> {
                    String body = handler.getString(0, handler.length());
                    System.out.println("response: " + body);
                    responseFuture.complete(body);
                }),
                error -> {
                    System.out.println("error:" + error);
                    responseFuture.complete(error);
                }
        );
        request.end();
        while (!responseFuture.isDone()) {
            Thread.sleep(50);
        }

    }

}