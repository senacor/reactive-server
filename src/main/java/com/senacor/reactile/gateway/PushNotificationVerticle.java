package com.senacor.reactile.gateway;

import com.senacor.reactile.service.appointment.AppointmentService;
import com.senacor.reactile.service.customer.CustomerService;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;

public class PushNotificationVerticle extends AbstractVerticle {

    public static final String PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE = "PushNotification#Customer#updateAddress#customerId=";
    public static final String PUBLISH_ADDRESS_APPOINTMENT_UPDATE = "PushNotification#Appointment#update#appointmentId=";
    public static final String PUBLISH_ADDRESS_APPOINTMENT_DELETE = "PushNotification#Appointment#delete#appointmentId=";
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationVerticle.class);

    @Override
    public void start() {
        registerCustomerAddressUpdateHandler();
        registerAppointmentUpdateHandler();
        registerAppointmentDeleteHandler();
    }

    private void registerCustomerAddressUpdateHandler() {
        vertx.eventBus().consumer(CustomerService.ADDRESS_EVENT_UPDATE_ADDRESS).toObservable().map(Message::body).cast(JsonObject.class)
            .subscribe(updateEvent -> {
                String publishAddress = PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE + updateEvent.getString("id");
                logger.info("publish event on Address: " + publishAddress);
                vertx.eventBus().publish(publishAddress, updateEvent);
            }, throwable -> logger.error("Error while handling event from " + CustomerService.ADDRESS_EVENT_UPDATE_ADDRESS, throwable));
    }

    private void registerAppointmentUpdateHandler() {
        vertx.eventBus().consumer(AppointmentService.ADDRESS_EVENT_UPDATE_APPOINTMENT).toObservable().map(Message::body).cast(JsonObject.class)
            .subscribe(updateEvent -> {
                String publishAddress = PUBLISH_ADDRESS_APPOINTMENT_UPDATE + updateEvent.getString("id");
                logger.info("publish event on Appointment: " + publishAddress);
                vertx.eventBus().publish(publishAddress, updateEvent);
            }, throwable -> logger.error("Error while handling event from " + AppointmentService.ADDRESS_EVENT_UPDATE_APPOINTMENT, throwable));
    }

    private void registerAppointmentDeleteHandler() {
        vertx.eventBus().consumer(AppointmentService.ADDRESS_EVENT_DELETE_APPOINTMENT).toObservable().map(Message::body).cast(JsonObject.class)
            .subscribe(updateEvent -> {
                String publishAddress = PUBLISH_ADDRESS_APPOINTMENT_DELETE + updateEvent.getString("id");
                logger.info("publish event on Appointment: " + publishAddress);
                vertx.eventBus().publish(publishAddress, updateEvent);
            }, throwable -> logger.error("Error while handling event from " + AppointmentService.ADDRESS_EVENT_DELETE_APPOINTMENT, throwable));
    }

}
