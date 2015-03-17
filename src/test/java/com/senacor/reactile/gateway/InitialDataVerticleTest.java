package com.senacor.reactile.gateway;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.core.json.JsonObject;
import org.junit.ClassRule;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InitialDataVerticleTest {

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.values()).deployVerticle(InitialDataVerticle.class);

    private final ObservableMongoService mongoService = ObservableMongoService.from(vertxRule.vertx());

    @Test
    public void thatDataIsGenerated() {

        Long count = mongoService.count("customers", new JsonObject()).toBlocking().first();
        assertThat(count, is((long)InitialDataVerticle.COUNT));

    }



}