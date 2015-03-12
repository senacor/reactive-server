package com.senacor.reactile;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.account.AccountId;
import com.senacor.reactile.account.CreditCard;
import com.senacor.reactile.account.CreditCardId;
import com.senacor.reactile.account.Currency;
import com.senacor.reactile.account.Transaction;
import com.senacor.reactile.account.TransactionId;
import com.senacor.reactile.codec.ArrayListObjectMessageCodec;
import com.senacor.reactile.bootstrap.VerticleDeployer;
import com.senacor.reactile.codec.DomainObjectMessageCodec;
import com.senacor.reactile.codec.ValueObjectMessageCodec;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Contact;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.user.User;
import com.senacor.reactile.user.UserId;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.rules.ExternalResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

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
        verticleDeployer.deploy(10_000);
    }


    @Override
    protected void after() {
        verticleDeployer.stop(10_000);
        vertx.close();
    }


    private void registerDomainObjectCodec() {
        Stream.of(
                User.class,
                Address.class,
                Contact.class,
                Country.class,
                Customer.class,
                Account.class,
                CreditCard.class,
                Transaction.class
        )
                .forEach(clazz -> ((io.vertx.core.eventbus.EventBus) vertx.eventBus().getDelegate()).registerDefaultCodec(clazz, DomainObjectMessageCodec.from(clazz)));
        Stream.of(
                ArrayList.class
        )
                .forEach(clazz -> ((io.vertx.core.eventbus.EventBus) vertx.eventBus().getDelegate()).registerDefaultCodec(clazz, new ArrayListObjectMessageCodec()));
        Stream.of(
                UserId.class,
                CustomerId.class,
                AccountId.class,
                CreditCardId.class,
                TransactionId.class,
                Currency.class
        )
                .forEach(clazz -> ((io.vertx.core.eventbus.EventBus) vertx.eventBus().getDelegate()).registerDefaultCodec(clazz, ValueObjectMessageCodec.from(clazz)));
    }

    public <T> Message<T> sendBlocking(String address, Object message) throws InterruptedException, ExecutionException, TimeoutException {
        return blockingEventBus.sendObservable(address, message);
    }

    public <T> Message<T> sendBlocking(String address, Object message, String action) throws InterruptedException, ExecutionException, TimeoutException {
        return blockingEventBus.sendObservable(address, message, action);
    }

    public <T> Message<T> sendBlocking(String address, Object message, String action, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return blockingEventBus.sendObservable(address, message, action, timeout);
    }

    public <T> Message<T> sendBlocking(String address, Object message, DeliveryOptions options, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return blockingEventBus.sendObservable(address, message, options, timeout);
    }
}
