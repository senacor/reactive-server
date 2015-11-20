package com.senacor.reactile.gateway;

import com.google.common.base.Throwables;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.appointment.AppointmentFixtures;
import com.senacor.reactile.service.appointment.AppointmentService;
import com.senacor.reactile.service.appointment.CustomerAppointmentChangedEvt;
import com.senacor.reactile.service.customer.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PushNotificationVerticleTest {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationVerticleTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.CustomerService, Services.AppointmentService).deployVerticle(PushNotificationVerticle.class);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private CustomerService service;

    @Inject
    private AppointmentService appointmentService;


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


    @Test
    public void testAppointmentUpdateEvent() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();

        final LinkedBlockingQueue<CustomerAppointmentChangedEvt> queue = new LinkedBlockingQueue<>();

        // listen for events
        String eventAddress = PushNotificationVerticle.PUBLISH_ADDRESS_CUSTOMER_APPOINTMENT_UPDATE + customer.getId().getId();
        logger.info("listening on address '" + eventAddress + "'");
        vertxRule.eventBus().consumer(eventAddress)
                .toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .map(CustomerAppointmentChangedEvt::fromJson)
                .subscribe(queue::add,
                        throwable -> fail(throwable.getMessage() + Throwables.getStackTraceAsString(throwable)));

        // create customer and update Address
        service.createCustomer(customer)
                .map(customerCreated ->
                        AppointmentFixtures.newAppointmentForCustomer(customer.getId().getId(), "cust-app" + customer.getId().getId()))
                .flatMap(newAppointment -> appointmentService.createOrUpdateAppointment(newAppointment))
                .subscribe(customerWithAppointment -> logger.info("updateAppointment: " + customerWithAppointment));

        CustomerAppointmentChangedEvt event = queue.poll(5L, TimeUnit.SECONDS);
        logger.info("Received Event: " + event);
        assertNotNull("CustomerAppointmentChangedEvt not received", event);
        assertEquals("AppointmentId", "cust-app" + customer.getId().getId(), event.getAppointment().getId().getId());
    }

}