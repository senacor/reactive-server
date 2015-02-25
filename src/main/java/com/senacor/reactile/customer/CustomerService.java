package com.senacor.reactile.customer;

import com.senacor.reactile.auth.UserId;
import com.senacor.reactile.gateway.GatewayServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;

import static com.senacor.reactile.customer.Address.anAddress;

public class CustomerService extends AbstractVerticle {


    public static final String ADDRESS = "customer";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<CustomerId> consumer = eventBus.consumer(ADDRESS);
        consumer.toObservable().subscribe(message -> message.reply(getCustomer(message.body().getId())));

        vertx.setPeriodic(1, tick -> eventBus.publish(GatewayServer.PUBLISH_ADDRESS, newCustomerChangedEvent()));

    }

    private Customer getCustomer(String id) {
        return Customer.newBuilder()
                .withId(id)
                .withTaxCountry(new Country("Deutschland", "DE"))
                .withTaxNumber("SSDS3242342342342")
                .build();
    }

    @Override
    public void stop() throws Exception {

    }

    private CustomerAddressChangedEvt newCustomerChangedEvent() {
        return new CustomerAddressChangedEvt(new UserId("momann"), new CustomerId("007"), anAddress()
                .withStreet("Erika-Mann-Straße")
                .withAddressNumber("55")
                .withCountry(new Country("DE", "Deutschland"))
                .build());
    }
}
