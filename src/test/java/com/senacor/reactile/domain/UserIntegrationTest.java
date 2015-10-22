package com.senacor.reactile.domain;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.gateway.InitialDataVerticle;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.http.HttpResponse;
import com.senacor.reactile.http.HttpTestClient;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static com.senacor.reactile.domain.HttpResponseMatchers.hasHeader;
import static com.senacor.reactile.domain.HttpResponseMatchers.hasStatus;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperties;
import static org.junit.Assert.assertThat;


public class UserIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UserIntegrationTest.class);

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.GatewayService).deployVerticle(InitialDataVerticle.class);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);


    private final HttpTestClient httpClient = new HttpTestClient(vertxRule.vertx());

    @Test
    public void thatUserRead() throws Exception {
        HttpResponse response = httpClient.get("/users/momann");
        assertThat(response, hasStatus(200));
        logger.info("header: " + response.headersAsString());
        assertThat(response, hasHeader("content-length"));
        assertThat(response, hasHeader("Access-Control-Allow-Origin", "*"));
        assertThat(response, hasHeader("x-response-time"));

        JsonObject json = response.asJson();
        logger.info("response json: " + json.encodePrettily());

        assertThat(json, hasProperties("firstName", "lastName", "branchId"));
    }


}