package com.senacor.reactile.gateway;

import com.senacor.reactile.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.user.User;
import com.senacor.reactile.user.UserService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import javax.inject.Inject;

public class PushNotificationVerticle extends AbstractVerticle {

    public static final String PUBLISH_ADDRESS = "EventPump";
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final UserService userService;

    @Inject
    public PushNotificationVerticle(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void start() {
        registerEventSubcriber();
    }

    private void registerEventSubcriber() {
        vertx.eventBus().consumer(PUBLISH_ADDRESS).toObservable()
                .map(message -> (CustomerAddressChangedEvt) message.body())
                .flatMap(event -> {
                    Observable<User> userObservable = userService.getUser(event.getUserId());
                    return userObservable.map(event::replaceUser);
                })
                .subscribe(eventWithUser -> log.info("Received event " + eventWithUser));
    }
}
