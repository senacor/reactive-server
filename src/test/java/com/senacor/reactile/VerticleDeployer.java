package com.senacor.reactile;

import com.google.common.base.Throwables;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.Vertx;
import org.junit.After;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VerticleDeployer {


    private final Set<String> notStarted = new LinkedHashSet<>();
    private final Set<String> started = new LinkedHashSet<>();
    private final Vertx vertx;

    public VerticleDeployer(Vertx vertx) {
        this.vertx = vertx;
    }

    public void deployVerticles(long timeoutInMillis) {
        deployVerticles(timeoutInMillis, new DeploymentOptions());
    }

    public void deployVerticles(long timeoutInMillis, DeploymentOptions options) {
        notStarted.stream().map(identifier -> startVerticle(identifier, new DeploymentOptions(options))).map(future -> waitForCompletion(future, timeoutInMillis)).forEach(started::add);
    }

    private <T> T waitForCompletion(CompletableFuture<T> future, long timeoutInMillis) {
        try {
            return future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


    public void stopVerticles(long timeoutInMillis) {
        started.stream().map(this::stopVerticle).forEach(future -> waitForCompletion(future, timeoutInMillis));
    }

    private CompletableFuture<String> startVerticle(String identifier, DeploymentOptions options){
        CompletableFuture<String> deploymentIdFuture = new CompletableFuture<>();
        getVertxDelegate().deployVerticle(identifier, options, RxHelper.toFuture(
                deploymentId -> {
                    System.out.println("Start succeeded for " + identifier + " with DeploymentId " + deploymentId);
                    deploymentIdFuture.complete(deploymentId);
                }, failure -> {
                    System.out.println("Start failed for " + identifier + ". Cause: " + failure);
                    deploymentIdFuture.completeExceptionally(failure);
                }));
        return deploymentIdFuture;
    }

    @After
    private CompletableFuture<String> stopVerticle(String deploymentId) {
        CompletableFuture<String> undeploymentFuture = new CompletableFuture<>();
        getVertxDelegate().undeploy(deploymentId, RxHelper.toFuture(
                response -> {
                    System.out.println("Stop succeeded for DeploymentId " + deploymentId);
                    undeploymentFuture.complete(deploymentId);
                },
                failure -> {
                    System.out.println("Stop failed for DeploymentId " + deploymentId + ". Cause: " + failure);
                    undeploymentFuture.completeExceptionally(failure);
                }));
        return undeploymentFuture;
    }

    private io.vertx.core.Vertx getVertxDelegate() {
        return (io.vertx.core.Vertx) vertx.getDelegate();
    }

    public void addService(ServiceIdProvider serviceIdProvider) {
        notStarted.add(serviceIdProvider.getId());
    }

    public void addVerticle(Class<? extends Verticle> verticleClass) {
        notStarted.add(verticleClass.getName());
    }
}
