package com.senacor.reactile.gateway;

import com.senacor.reactile.HttpClientRule;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.user.UserServiceVerticle;
import com.senacor.reactile.customer.CustomerServiceVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.junit.Rule;
import org.junit.Test;

public class GatewayVerticleTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(GatewayVerticle.class, CustomerServiceVerticle.class, UserServiceVerticle.class);

    @Rule
    public final HttpClientRule httpClient = new HttpClientRule(vertxRule.vertx());

    @Test
    public void thatRequestsAreHandled() throws InterruptedException {
        HttpClientRequest request = httpClient.request(HttpMethod.GET, 8080, "localhost", "/start?user=momann&customerId=007");
        String content = httpClient.readBody(request);
        System.out.println(content);

    }

}