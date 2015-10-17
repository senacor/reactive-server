package com.senacor.reactile.gateway;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InitialDataVerticleTest {

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.values()).deployVerticle(InitialDataVerticle.class);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private MongoService mongoService;

    @Test
    public void thatDataIsGenerated() {

        Long count = mongoService.countObservable("customers", new JsonObject()).toBlocking().first();
        assertThat(count, is((long)InitialDataVerticle.COUNT));

    }



}