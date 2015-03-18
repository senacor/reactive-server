package com.senacor.reactile.gateway;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.http.HttpResponse;
import com.senacor.reactile.http.HttpTestClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import org.junit.Rule;
import org.junit.Test;

import static com.senacor.reactile.domain.HttpResponseMatchers.hasHeader;
import static com.senacor.reactile.domain.HttpResponseMatchers.hasStatus;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperties;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasSize;
import static org.junit.Assert.assertThat;

public class GatewayVerticleTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(Services.GatewayService).deployVerticle(InitialDataVerticle.class);

    private final HttpTestClient httpClient = new HttpTestClient(Vertx.vertx());

    @Test
    public void thatRequestsAreHandled() throws Exception {
        HttpResponse response = httpClient.get("/start?user=momann&customerId=cust-100000");
        assertThat(response, hasStatus(200));
        assertThat(response, hasHeader("content-length"));
        assertThat(response, hasHeader("Access-Control-Allow-Origin", "*"));

        JsonObject json = response.asJson();
        System.out.println(json.encodePrettily());
        
        assertThat(json, hasProperties("customer", "branch", "appointments", "recommendations", "news"));
        JsonObject jsonCustomer = json.getJsonObject("customer");
        assertThat(jsonCustomer, hasProperties("products", "transactions"));
        assertThat(jsonCustomer.getJsonObject("products"), hasProperties("accounts", "creditCards"));

        JsonObject products = jsonCustomer.getJsonObject("products");
        JsonArray accounts = products.getJsonArray("accounts");
        assertThat(accounts, hasSize(1));
        JsonArray creditCards = products.getJsonArray("creditCards");
        assertThat(creditCards, hasSize(1));
    }

}