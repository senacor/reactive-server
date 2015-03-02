package com.senacor.reactile.gateway;

import com.senacor.reactile.HttpClientRule;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.MongoBootstrap;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.junit.Rule;
import org.junit.Test;

public class GatewayVerticleTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule();
    {
        vertxRule.deployVerticle(MongoBootstrap.class, GatewayVerticle.class);
        vertxRule.deployVerticle(Services.CustomerService, Services.UserConnector, Services.UserService);
    }

    @Rule
    public final HttpClientRule httpClient = new HttpClientRule(vertxRule.vertx());

    @Test
    public void thatRequestsAreHandled() throws InterruptedException {
        HttpClientRequest request = httpClient.request(HttpMethod.GET, 8080, "localhost", "/start?user=momann&customerId=08-cust-15");
        String content = httpClient.readBody(request);
        System.out.println(content);

    }

}