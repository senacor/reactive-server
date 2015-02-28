package com.senacor.reactile;

import com.google.common.base.Throwables;
import io.vertx.core.Verticle;
import io.vertx.rxjava.core.Vertx;
import org.junit.After;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VerticleDeployer {


    private final Set<String> notStarted = new HashSet<>();
    private final Set<String> started = new HashSet<>();
    private final Vertx vertx;

    public VerticleDeployer(Vertx vertx) {
        this.vertx = vertx;
    }

    public void deployVerticles(long timeoutInMillis) {
        notStarted.stream().map(this::startVerticle).map(future -> waitForCompletion(future, timeoutInMillis)).forEach(started::add);
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

    private CompletableFuture<String> startVerticle(String identifier){
        CompletableFuture<String> deploymentIdFuture = new CompletableFuture<>();
        vertx.deployVerticleObservable(identifier).subscribe(
                deploymentId -> {
                    System.out.println("Start succeeded for " + identifier + " with DeploymentId " + deploymentId);
                    deploymentIdFuture.complete(deploymentId);
                }, failure -> {
                    System.out.println("Start failed for " + identifier + ". Cause: " + failure);
                    deploymentIdFuture.completeExceptionally(failure);
                });
        return deploymentIdFuture;
    }

    @After
    private CompletableFuture<String> stopVerticle(String deploymentId) {
        CompletableFuture<String> undeploymentFuture = new CompletableFuture<>();
        vertx.undeployVerticleObservable(deploymentId).subscribe(
                response -> {
                    System.out.println("Stop succeeded for DeploymentId " + deploymentId);
                    undeploymentFuture.complete(deploymentId);
                },
                failure -> {
                    System.out.println("Stop failed for DeploymentId " + deploymentId + ". Cause: " + failure);
                    undeploymentFuture.completeExceptionally(failure);
                });
        return undeploymentFuture;
    }

    public void addService(ServiceIdProvider serviceIdProvider) {
        notStarted.add(serviceIdProvider.getId());
    }

    public void addVerticle(Class<? extends Verticle> verticleClass) {
        notStarted.add(verticleClass.getName());
    }
}
