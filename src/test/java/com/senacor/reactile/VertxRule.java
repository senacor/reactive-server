package com.senacor.reactile;

import io.vertx.core.AsyncResult;
import io.vertx.core.Verticle;
import io.vertx.rxjava.core.Vertx;
import org.junit.After;
import org.junit.rules.ExternalResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VertxRule extends ExternalResource {

    public static final String FAILED_ID = "FAILED";
    private final Vertx vertx = Vertx.vertx();

    private final Set<Class<? extends Verticle>> verticlesNotStarted = new HashSet<>();
    private final Set<String> verticlesStarted = new HashSet<>();

    public VertxRule(Class<? extends Verticle>... verticlesNotStarted) {
        Arrays.stream(verticlesNotStarted).forEach(this.verticlesNotStarted::add);
    }

    private void deployVerticle(Class<? extends Verticle> verticle) {
        try {
            String deploymentId = startVerticle(verticle);
            verticlesStarted.add(deploymentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Vertx vertx() {
        return vertx;
    }

    @Override
    protected void before() throws Throwable {
        verticlesNotStarted.forEach(verticle -> {
            try {
                String deploymentId = startVerticle(verticle);
                verticlesStarted.add(deploymentId);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    protected void after() {
        vertx.deployments().forEach(deploymentId -> {
            try {
                stopVerticle(deploymentId);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        vertx.close();
    }

    private String startVerticle(Class<? extends Verticle> verticle) throws Exception {
        String verticleId = verticle.getName();
        CompletableFuture<String> deploymentIdFuture = new CompletableFuture<>();
        vertx.deployVerticle(verticleId, response -> {
                    printResult(verticleId, response, "Start");
                    if (response.failed()) {
                        deploymentIdFuture.completeExceptionally(response.cause());
                    } else {
                        deploymentIdFuture.complete(response.result());
                    }
                }
        );
        return deploymentIdFuture.get(1, TimeUnit.SECONDS);
    }

    @After
    private void stopVerticle(String deploymentId) throws Exception {
        CompletableFuture<String> undeploymentFuture = new CompletableFuture<>();
        vertx.undeployVerticle(deploymentId, response -> {
                    if (response.succeeded()) {
                        System.out.println("Stop succeeded for DeploymentId " + deploymentId);
                        undeploymentFuture.complete(deploymentId);
                    } else if (response.failed()) {
                        System.out.println("Stop failed for DeploymentId " + deploymentId + ". Cause: " + response.cause());
                        undeploymentFuture.completeExceptionally(response.cause());
                    }
                }
        );
        undeploymentFuture.get(1, TimeUnit.SECONDS);
    }

    private void printResult(String verticleId, AsyncResult<String> response, final String operation) {
        if (response.succeeded()) {
            System.out.println(operation + " succeeded for Verticle " + verticleId);
        } else if (response.failed()) {
            System.out.println(operation + " failed for Verticle " + verticleId + ". Cause: " + response.cause());
        }
    }

}
