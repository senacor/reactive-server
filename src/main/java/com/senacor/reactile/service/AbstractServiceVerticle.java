package com.senacor.reactile.service;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.lang.reflect.Method;

public abstract class AbstractServiceVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start() throws Exception {
        subcribe();
    }

    private void subcribe() {
        vertx.eventBus().consumer(getAddress()).toObservable().subscribe(
                this::messageHandler,
                this::errorHandler);
    }

    private void errorHandler(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void messageHandler(Message<Object> message) {
        ServiceMetadata actions = ActionInspector.getServiceMetadata(this.getClass());
        setReplyHandler(message, actions);
    }

    private void setReplyHandler(Message<Object> message, ServiceMetadata actions) {
        Object payload = message.body();
        String action = message.headers().get("action");
        if (!actions.hasAction(action)) {
            throw new IllegalArgumentException("Unknown service operation " + action);
        }

        try {
            Method serviceMethod = actions.getAction(action);
            Observable<Object> serviceResult = (Observable<Object>) serviceMethod.invoke(this, payload);
            serviceResult.subscribe(
                    message::reply,
                    Throwable::printStackTrace
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //TODO read from config
    protected abstract String getAddress();

    protected Logger log() {
        return log;
    }
}
