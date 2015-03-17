package com.senacor.reactile.gateway;

import com.senacor.reactile.HttpClientRule;
import com.senacor.reactile.TestServices;
import com.senacor.reactile.VertxRule;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.junit.Rule;
import org.junit.Test;

public class GatewayVerticleTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(TestServices.GatewayService).deployVerticle(InitialDataVerticle.class);

    @Rule
    public final HttpClientRule httpClient = new HttpClientRule(vertxRule.vertx());

    @Test
    public void thatRequestsAreHandled() throws InterruptedException {
        HttpClientRequest request = httpClient.request(HttpMethod.GET, "/start?user=momann&customerId=cust-100000");
        String content = httpClient.readBody(request);
        System.out.println(new JsonObject(content).encodePrettily());

    }

}