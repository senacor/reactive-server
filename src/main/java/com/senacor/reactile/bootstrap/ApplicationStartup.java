package com.senacor.reactile.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartup extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        JsonObject config = new JsonObject();
        config.put("address", "127.0.0.1").put("port", 27018);
        // config.put("username", "john").put("password", "passw0rd");
        vertx.deployVerticle("io.vertx.ext.mongo.MongoServiceVerticle", new DeploymentOptions().setConfig(config), response -> {
            if (response.succeeded()) {
                System.out.println("mongo-service startet: " + response.result());
            } else {
                System.out.println("mongo-service failed: " + response.cause());
                throw new RuntimeException("mongo-service failed: " + response.cause());
            }
        });
    }
}
