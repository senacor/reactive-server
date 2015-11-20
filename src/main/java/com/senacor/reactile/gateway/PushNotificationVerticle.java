package com.senacor.reactile.gateway;

import com.senacor.reactile.service.appointment.AppointmentService;
import com.senacor.reactile.service.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.service.customer.CustomerService;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;

public class PushNotificationVerticle extends AbstractVerticle {

    public static final String PUBLISH_ADDRESS = "EventPump";
    public static final String PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE = "PushNotification#Customer#updateAddress#customerId=";
    public static final String PUBLISH_ADDRESS_CUSTOMER_APPOINTMENT_UPDATE = "PushNotification#Customer#updateAppointment#customerId=";
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationVerticle.class);


    @Override
    public void start() {
        registerEventSubcriber();
        registerCustomerAddressUpdateHandler();
        registerCustomerAppointmentHandler();
    }

    private void registerCustomerAppointmentHandler() {
        vertx.eventBus().consumer(AppointmentService.APPOINTMENT_EVENT_UPDATE_ADDRESS).toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .subscribe(updateEvent -> {
                    String publishAppointment = PUBLISH_ADDRESS_CUSTOMER_APPOINTMENT_UPDATE + updateEvent.getString("id");
                    logger.info("publish event on Appointment: " + publishAppointment);
                    vertx.eventBus().publish(publishAppointment, updateEvent);
                }, throwable -> logger.error("Error while handling event from " + AppointmentService
                        .APPOINTMENT_EVENT_UPDATE_ADDRESS, throwable));
    }

    private void registerCustomerAddressUpdateHandler() {
        vertx.eventBus().consumer(CustomerService.ADDRESS_EVENT_UPDATE_ADDRESS).toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .subscribe(updateEvent -> {
                    String publishAddress = PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE + updateEvent.getString("id");
                    logger.info("publish event on Address: " + publishAddress);
                    vertx.eventBus().publish(publishAddress, updateEvent);
                }, throwable -> logger.error("Error while handling event from " + CustomerService
                        .ADDRESS_EVENT_UPDATE_ADDRESS, throwable));
    }

    private void registerEventSubcriber() {
        vertx.eventBus().consumer(PUBLISH_ADDRESS).toObservable()
                .map(message -> (CustomerAddressChangedEvt) message.body())
                .subscribe(eventWithUser -> logger.info("Received event " + eventWithUser));
    }

}
