package com.senacor.reactile.gateway;

import com.google.common.base.Throwables;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.service.customer.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class PushNotificationVerticleTest {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationVerticleTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.CustomerService).deployVerticle(PushNotificationVerticle.class);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private CustomerService service;

    @Test
    public void testCustomerUpdateEvent() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();

        final LinkedBlockingQueue<CustomerAddressChangedEvt> queue = new LinkedBlockingQueue<>();

        // listen for events
        String eventAddress = PushNotificationVerticle.PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE + customer.getId().getId();
        logger.info("listening on address '" + eventAddress + "'");
        vertxRule.eventBus().consumer(eventAddress)
                .toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .map(CustomerAddressChangedEvt::fromJson)
                .subscribe(queue::add,
                        throwable -> fail(throwable.getMessage() + Throwables.getStackTraceAsString(throwable)));

        // create customer and update Address
        service.createCustomer(customer)
                .map(customerCreated -> Address.anAddress()
                        .withAddress(customerCreated.getAddresses().get(0))
                        .withZipCode("00815")
                        .withCity("NewCity")
                        .build())
                .flatMap(newAddress -> service.updateAddress(customer.getId(), newAddress))
                .subscribe(customerWithUpdatedAddress -> logger.info("updateAddress: " + customerWithUpdatedAddress));

        CustomerAddressChangedEvt event = queue.poll(5L, TimeUnit.SECONDS);
        logger.info("Received Event: " + event);
        assertNotNull("CustomerAddressChangedEvt not received", event);
        assertEquals("event.newAddress.city", "NewCity", event.getNewAddress().getCity());
    }

}