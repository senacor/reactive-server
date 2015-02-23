package com.senacor.reactile.gateway;

import io.vertx.core.AsyncResult;
import io.vertx.core.Verticle;
import io.vertx.rxjava.core.Vertx;
import org.junit.After;
import org.junit.rules.ExternalResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class VertxRule extends ExternalResource {

    private final Vertx vertx = Vertx.vertx();

    private final Set<Class<? extends Verticle>> verticlesNotStarted = new HashSet<>();
    private final Set<Class<? extends Verticle>> verticlesStarted = new HashSet<>();

    public VertxRule(Class<? extends Verticle>... verticlesNotStarted) {
        Arrays.stream(verticlesNotStarted).forEach(this.verticlesNotStarted::add);
    }

    private void deployVerticle(Class<? extends Verticle> verticle) {
        try {
            startVerticle(verticle);
            verticlesStarted.add(verticle);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void undeployVerticle(Class<? extends Verticle> verticle) {
        try {
            stopVerticle(verticle);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verticlesStarted.remove(verticle);

    }

    public Vertx vertx() {
        return vertx;
    }

    @Override
    protected void before() throws Throwable {
        verticlesNotStarted.forEach(this::deployVerticle);
    }


    @Override
    protected void after() {
        verticlesStarted.forEach(this::undeployVerticle);
    }

    private void startVerticle(Class<? extends Verticle> verticle) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String verticleId = verticle.getName();
        vertx.deployVerticle(verticleId, response -> {
                    printResult(verticleId, response, "Start");
                    countDownLatch.countDown();
                }
        );
        countDownLatch.await();
    }

    @After
    private void stopVerticle(Class<? extends Verticle> verticle) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String verticleId = verticle.getName();
        vertx.undeployVerticle(verticleId, response -> {
                    if (response.succeeded()) {
                        System.out.println("Stop succeeded for Verticle " + verticleId);
                    } else if (response.failed()) {
                        System.out.println("Stop failed for Verticle " + verticleId + ". Cause: " + response.cause());
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
