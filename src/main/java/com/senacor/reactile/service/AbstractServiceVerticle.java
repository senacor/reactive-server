package com.senacor.reactile.service;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractServiceVerticle extends AbstractVerticle {

    public static final String ADDRESS_KEY = "address";
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        checkState(config().containsKey("address"), String.format("Missing config key %s for Verticle %s", this.getClass(), ADDRESS_KEY));
    }

    @Override
    public void start() throws Exception {
        subcribe();
    }

    private void subcribe() {
        vertx.eventBus().consumer(config().getString(ADDRESS_KEY)).toObservable().subscribe(
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

    protected Logger log() {
        return log;
    }
}
