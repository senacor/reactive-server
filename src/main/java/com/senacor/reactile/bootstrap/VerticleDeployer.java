package com.senacor.reactile.bootstrap;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.senacor.reactile.ServiceIdProvider;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.Vertx;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VerticleDeployer {

    // TODO: hier temporaer DEFAULT_TIMEOUT auf z.B. 300_000 setzen, damit die lokale MongoDB herunterladen werden kann
    private final static long DEFAULT_TIMEOUT = 5_000;

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
            // TODO: hier temporaer DEFAULT_TIMEOUT verwenden, damit die lokale MongoDB herunterladen werden kann
            return future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


    public void undeploy(long timeoutInMillis) {
        reverseStarted().stream().map(this::stopVerticle).forEach(future -> waitForCompletion(future, timeoutInMillis));
    }

    public void undeploy(Future<Void> stopFuture) {
        reverseStarted().stream()
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

    private LinkedList<String> reverseStarted() {
        LinkedList<String> reverseStartOrderedList = new LinkedList<>(started);
        Collections.reverse(reverseStartOrderedList);
        return reverseStartOrderedList;
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
        Set<ServiceIdProvider> merged = Sets.newLinkedHashSet();
        merged.add(serviceIdProvider);
        merged.addAll(Arrays.asList(more));
        flattenDependencies(merged).stream()
                .sorted(ServiceIdProvider.comparator())
                .map(provider -> provider.getId())
                .forEach(notStarted::add);
    }

    private Set<ServiceIdProvider> flattenDependencies(Set<ServiceIdProvider> providers) {
        Set<ServiceIdProvider> flattened = new LinkedHashSet<>();
        for (ServiceIdProvider provider : providers) {
            flattened.add(provider);
            if (provider.hasDependencies()) {
                flattened.addAll(flattenDependencies(provider.dependsOn()));
            }
        }
        return flattened;
    }

    public void addVerticle(Class<? extends Verticle> verticleClass, Class<? extends Verticle>... more) {
        notStarted.add("guice:" + verticleClass.getName());
        Arrays.stream(more).map(clazz -> "guice:" + clazz.getName()).forEach(notStarted::add);
    }

}
