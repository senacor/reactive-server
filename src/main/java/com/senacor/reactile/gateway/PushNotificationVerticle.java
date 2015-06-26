package com.senacor.reactile.gateway;

import javax.inject.Inject;

import com.senacor.reactile.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.customer.CustomerService;
import com.senacor.reactile.newsticker.NewsServiceVerticle;
import com.senacor.reactile.user.User;
import com.senacor.reactile.user.UserService;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class PushNotificationVerticle extends AbstractVerticle {

    public static final String PUBLISH_ADDRESS = "EventPump";
    public static final String PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE = "PushNotification#Customer#updateAddress#customerId=";
    public static final String PUBLISH_NEWS_UPDATE = "PushNotification#News#update";
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationVerticle.class);


    private final UserService userService;

    @Inject
    public PushNotificationVerticle(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void start() {
        registerEventSubcriber();
        registerCustomerAddressUpdateHandler();
        registerNewsUpdateHandler();
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
                .flatMap(event -> {
                    Observable<User> userObservable = userService.getUser(event.getUserId());
                    return userObservable.map(event::replaceUser);
                })
                .subscribe(eventWithUser -> logger.info("Received event " + eventWithUser));
    }

    private void registerNewsUpdateHandler() {
        vertx.eventBus().consumer(NewsServiceVerticle.ADDRESS).toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .subscribe(updateEvent -> {
                    String publishAddress = PUBLISH_NEWS_UPDATE;
                    logger.info("publish event on Address: " + publishAddress);
                    vertx.eventBus().publish(publishAddress, updateEvent);
                }, throwable -> logger.error("Error while handling event from " + NewsServiceVerticle.ADDRESS, throwable));
    }
}
