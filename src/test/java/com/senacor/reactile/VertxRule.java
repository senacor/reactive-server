package com.senacor.reactile;

import com.senacor.reactile.bootstrap.VerticleDeployer;
import com.senacor.reactile.codec.Codecs;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.rules.ExternalResource;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class VertxRule extends ExternalResource {

    private final Vertx vertx = Vertx.vertx();
    private final BlockingEventBus blockingEventBus = new BlockingEventBus(vertx);
    private final VerticleDeployer verticleDeployer = new VerticleDeployer(vertx);


    public VertxRule() {
    }

    public VertxRule(ServiceIdProvider... deployVerticles) {
        Arrays.stream(deployVerticles).forEach(this.verticleDeployer::addService);
    }

    public VertxRule(Class<? extends Verticle>... deployVerticles) {
        Arrays.stream(deployVerticles).forEach(this.verticleDeployer::addVerticle);
    }

    public VertxRule deployVerticle(ServiceIdProvider verticle, ServiceIdProvider... moreVerticles) {
        verticleDeployer.addService(verticle);
        for (ServiceIdProvider v : moreVerticles) {
            verticleDeployer.addService(v);
        }
        return this;

    }

    public VertxRule deployVerticle(Class<? extends Verticle> verticle, Class<? extends Verticle>... moreVerticles) {
        verticleDeployer.addVerticle(verticle);
        for (Class<? extends Verticle> v : moreVerticles) {
            verticleDeployer.addVerticle(v);
        }
        return this;
    }

    public Vertx vertx() {
        return vertx;
    }

    public EventBus eventBus() {
        return vertx.eventBus();
    }

    @Override
    protected void before() throws Throwable {
        registerDomainObjectCodec();
        verticleDeployer.deploy(15_000);
    }


    @Override
    protected void after() {
        verticleDeployer.stop(15_000);
        vertx.close();
    }


    private void registerDomainObjectCodec() {
        Codecs.load((io.vertx.core.eventbus.EventBus) eventBus().getDelegate());
    }

    public <T> Message<T> sendBlocking(String address, Object message) throws InterruptedException, ExecutionException, TimeoutException {
        return blockingEventBus.<T>sendObservable(address, message);
    }

    public <T> Message<T> sendBlocking(String address, Object message, String action) throws InterruptedException, ExecutionException, TimeoutException {
        return blockingEventBus.<T>sendObservable(address, message, action);
    }

    public <T> Message<T> sendBlocking(String address, Object message, String action, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return blockingEventBus.<T>sendObservable(address, message, action, timeout);
    }

    public <T> Message<T> sendBlocking(String address, Object message, DeliveryOptions options, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return blockingEventBus.<T>sendObservable(address, message, options, timeout);
    }
}
