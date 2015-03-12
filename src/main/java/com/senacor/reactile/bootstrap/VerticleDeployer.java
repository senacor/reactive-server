package com.senacor.reactile.bootstrap;

import com.google.common.base.Throwables;
import com.senacor.reactile.ServiceIdProvider;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.Vertx;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VerticleDeployer {

    private final static long DEFAULT_TIMEOUT = 10_000;

    private final Set<String> notStarted = new LinkedHashSet<>();
    private final Set<String> started = new LinkedHashSet<>();
    private final Vertx vertx;

    public VerticleDeployer(Vertx vertx) {
        this.vertx = vertx;
    }

    public void deploy(Future<Void> startFuture) {
        try {
            deploy(DEFAULT_TIMEOUT);
            startFuture.complete();
        } catch (RuntimeException e) {
            startFuture.fail(e.getCause());
        }
    }


    public void deploy(long timeoutInMillis) {
        deploy(timeoutInMillis, new DeploymentOptions());
    }

    public void deploy(long timeoutInMillis, DeploymentOptions options) {
        notStarted.stream().map(identifier -> startVerticle(identifier, new DeploymentOptions(options)))
                .map(future -> waitForCompletion(future, timeoutInMillis))
                .forEach(started::add);
    }

    private <T> T waitForCompletion(CompletableFuture<T> future, long timeoutInMillis) {
        try {
            return future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


    public void stop(long timeoutInMillis) {
        started.stream().map(this::stopVerticle).forEach(future -> waitForCompletion(future, timeoutInMillis));
    }

    public void stop(Future<Void> stopFuture) {
        started.stream()
                .map(this::stopVerticle)
                .map(future -> future.handle((String id, Throwable ex) -> {
                            if (ex != null) {
                                stopFuture.fail(ex);
                                future.completeExceptionally(ex);
                            } else {
                                stopFuture.complete();
                                future.complete(id);
                            }
                            return future;
                        }
                )).forEach(future -> waitForCompletion(future, DEFAULT_TIMEOUT));
    }

    private CompletableFuture<String> startVerticle(String identifier, DeploymentOptions options) {
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

    public void addService(ServiceIdProvider serviceIdProvider, ServiceIdProvider... more) {
        notStarted.add(serviceIdProvider.getId());
        Arrays.stream(more).map(provider -> provider.getId()).forEach(notStarted::add);
    }

    public void addVerticle(Class<? extends Verticle> verticleClass, Class<? extends Verticle>... more) {
        notStarted.add(verticleClass.getName());
        Arrays.stream(more).map(clazz -> clazz.getName()).forEach(notStarted::add);
    }

}
