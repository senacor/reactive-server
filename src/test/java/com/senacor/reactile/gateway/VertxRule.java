package com.senacor.reactile.gateway;

import io.vertx.core.AsyncResult;
import io.vertx.core.Verticle;
import io.vertx.rxjava.core.Vertx;
import org.junit.After;
import org.junit.rules.ExternalResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

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
        } catch (InterruptedException e) {
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }


    @Override
    protected void after() {
        vertx.deployments().forEach(deploymentId -> {
            try {
                stopVerticle(deploymentId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        vertx.close();
    }

    private String startVerticle(Class<? extends Verticle> verticle) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String verticleId = verticle.getName();
        Optional<String> optional = Optional.empty();
        vertx.deployVerticle(verticleId, response -> {
                    printResult(verticleId, response, "Start");
                    countDownLatch.countDown();
                    optional.orElse(response.result());
                }
        );
        countDownLatch.await();
        return optional.orElse(FAILED_ID);
    }

    @After
    private void stopVerticle(String deploymentId) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        vertx.undeployVerticle(deploymentId, response -> {
                    if (response.succeeded()) {
                        System.out.println("Stop succeeded for DeploymentId " + deploymentId);
                    } else if (response.failed()) {
                        System.out.println("Stop failed for DeploymentId " + deploymentId + ". Cause: " + response.cause());
                    }
                    countDownLatch.countDown();
                }
        );
        countDownLatch.await();
    }

    private void printResult(String verticleId, AsyncResult<String> response, final String operation) {
        if (response.succeeded()) {
            System.out.println(operation + " succeeded for Verticle " + verticleId);
        } else if (response.failed()) {
            System.out.println(operation + " failed for Verticle " + verticleId + ". Cause: " + response.cause());
        }
    }

}
