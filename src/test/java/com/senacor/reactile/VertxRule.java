package com.senacor.reactile;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.account.AccountId;
import com.senacor.reactile.account.CreditCard;
import com.senacor.reactile.account.CreditCardId;
import com.senacor.reactile.account.Currency;
import com.senacor.reactile.codec.DomainObjectMessageCodec;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Contact;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.user.User;
import com.senacor.reactile.user.UserId;
import io.vertx.core.Verticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import org.junit.rules.ExternalResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class VertxRule extends ExternalResource {

    private final Vertx vertx = Vertx.vertx();
    private final VerticleDeployer verticleDeployer = new VerticleDeployer(vertx);


    public VertxRule() {
    }

    public VertxRule(ServiceIdProvider... deployVerticles) {
        Arrays.stream(deployVerticles).forEach(this.verticleDeployer::addService);
    }

    public VertxRule(Class<? extends Verticle>... deployVerticles) {
        Arrays.stream(deployVerticles).forEach(this.verticleDeployer::addVerticle);
    }

    public void deployVerticle(ServiceIdProvider verticle, ServiceIdProvider... moreVerticles) {
        verticleDeployer.addService(verticle);
        for (ServiceIdProvider v : moreVerticles) {
            verticleDeployer.addService(v);
        }

    }

    public void deployVerticle(Class<? extends Verticle> verticle, Class<? extends Verticle>... moreVerticles) {
        verticleDeployer.addVerticle(verticle);
        for (Class<? extends Verticle> v : moreVerticles) {
            verticleDeployer.addVerticle(v);
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
        verticleDeployer.deployVerticles(10_000);

    }


    @Override
    protected void after() {
        verticleDeployer.stopVerticles(10_000);
        vertx.close();
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
                CustomerAddressChangedEvt.class,
                ArrayList.class
        )
                .forEach(clazz -> ((io.vertx.core.eventbus.EventBus) vertx.eventBus().getDelegate()).registerDefaultCodec(clazz, DomainObjectMessageCodec.from(clazz)));
    }

}
