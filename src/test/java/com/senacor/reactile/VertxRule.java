package com.senacor.reactile;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.account.AccountId;
import com.senacor.reactile.account.CreditCard;
import com.senacor.reactile.account.CreditCardId;
import com.senacor.reactile.account.Currency;
import com.senacor.reactile.auth.User;
import com.senacor.reactile.auth.UserId;
import com.senacor.reactile.codec.DomainObjectMessageCodec;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Contact;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.customer.CustomerId;
import io.vertx.core.AsyncResult;
import io.vertx.core.Verticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import org.junit.After;
import org.junit.rules.ExternalResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class VertxRule extends ExternalResource {

    private final Vertx vertx = Vertx.vertx();

    private final Set<Class<? extends Verticle>> verticlesNotStarted = new HashSet<>();
    private final Set<String> verticlesStarted = new HashSet<>();

    public VertxRule(Class<? extends Verticle>... deployVerticles) {
        Arrays.stream(deployVerticles).forEach(this.verticlesNotStarted::add);
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

    public EventBus eventBus() {
        return vertx.eventBus();
    }

    @Override
    protected void before() throws Throwable {
        registerDomainObjectCodec();
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
        return deploymentIdFuture.get(3, TimeUnit.SECONDS);
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
        undeploymentFuture.get(3, TimeUnit.SECONDS);
    }

    private void registerDomainObjectCodec() {
        Stream.of(
                User.class,
                UserId.class,
                Address.class,
                Contact.class,
                Country.class,
                Customer.class,
                CustomerId.class,
                Account.class,
                AccountId.class,
                CreditCard.class,
                CreditCardId.class,
                Currency.class,
                CustomerAddressChangedEvt.class
        )
                .forEach(clazz -> ((io.vertx.core.eventbus.EventBus) vertx.eventBus().getDelegate()).registerDefaultCodec(clazz, DomainObjectMessageCodec.from(clazz)));
    }

    private static void printResult(String verticleId, AsyncResult<String> response, final String operation) {
        if (response.succeeded()) {
            System.out.println(operation + " succeeded for Verticle " + verticleId);
        } else if (response.failed()) {
            System.out.println(operation + " failed for Verticle " + verticleId + ". Cause: " + response.cause());
        }
    }

}
