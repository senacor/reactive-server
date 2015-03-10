package com.senacor.reactile.mongo;

import com.senacor.reactile.Services;
import com.senacor.reactile.VerticleDeployer;
import io.vertx.rxjava.core.Vertx;
import org.junit.rules.ExternalResource;


public class EmbeddedMongoRule extends ExternalResource {

    private final VerticleDeployer verticleDeployer;

    public EmbeddedMongoRule(Vertx vertx) {
        verticleDeployer = new VerticleDeployer(vertx);
        verticleDeployer.addService(Services.EmbeddedMongo);
    }

    @Override
    public void before() throws Throwable {
        verticleDeployer.deployVerticles(60 * 1000);
    }

    @Override
    public void after() {
        verticleDeployer.stopVerticles(60 * 1000);
    }
}
