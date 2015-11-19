package com.senacor.reactile.abstractservice;

import com.senacor.reactile.json.Jsonizable;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageProducer;
import rx.Observable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractServiceVerticle extends AbstractVerticle {

    public static final String ADDRESS_KEY = "address";
    public static final String ACTION_HEADER = "action";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    ServiceActionRegistry actionRegistry;
    Object serviceInstance;

    public AbstractServiceVerticle(Object serviceInstance) {
        actionRegistry = ServiceActionRegistry.getFromClass(serviceInstance.getClass().getInterfaces()[0]);
        this.serviceInstance = serviceInstance;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        checkState(config().containsKey(ADDRESS_KEY), String.format("Missing config key %s for Verticle %s", ADDRESS_KEY, this.getClass().getName()));
    }

    @Override
    public void start() throws Exception {
        log.info("Starting Verticle: " + config().getString("address"));
        String address = config().getString("address");
        if (address == null) {
            throw new IllegalStateException("address field must be specified in config for CustomerService");
        }
        subscribeMethodsToMessageBus();
        subscribeMessageBusToPublishingMethods();
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping Verticle: " + config().getString("address"));
    }

    private void subscribeMethodsToMessageBus() {
        String adress = config().getString(ADDRESS_KEY);
        vertx.eventBus().consumer(adress).toObservable().subscribe(
                this::messageHandler,
                this::errorHandler);
    }

    private void subscribeMessageBusToPublishingMethods() {

        //ermittle alle PublishSubscribe Methoden
        List<Method> actionsForPattern = actionRegistry.getActionsForPattern(Action.MessagePattern.PublishSubsrcribe);
        actionsForPattern.forEach(method-> {
                    Observable<Object> publishObservable = invokeObservableMethod(method, null);

                    //registriere Producer
                    MessageProducer<Object> prod = vertx.eventBus().publisher(config().getString(ADDRESS_KEY)+"."+method.getName());

                    //TODO:was ist mit fehlern
                    //subscribe Message Producer an Observable
                    publishObservable
                            .doOnNext((news) -> {
                                System.out.println("Send to Eventbus " + news);
                            })
                            .map(response -> serializeResponse(response))
                            .subscribe(prod::write,
                                    throwable -> throwable.printStackTrace());
                }
        );

    }

    private void errorHandler(Throwable throwable) {
        System.err.println(throwable.getMessage());
        throwable.printStackTrace();
    }

    private void messageHandler(Message<Object> message) {
        Object payload = message.body();

        String action = getActionNameFromMessageHeader(message);
        Method serviceMethod = actionRegistry.getAction(action);

        Observable<Object> invokationObservable = invokeObservableMethod(serviceMethod, payload);

        setReplyHandler(message, serviceMethod, invokationObservable);

    }

    private Observable<Object> invokeObservableMethod(Method serviceMethod, Object payload) {
        Observable<Object> invokeResult = null;

        try {
            Object[] args = deserializeParameter(serviceMethod, (JsonArray) payload);

            if(args != null){
                invokeResult = (Observable<Object>) serviceMethod.invoke(serviceInstance, args);
            } else {
                invokeResult = (Observable<Object>) serviceMethod.invoke(serviceInstance);
            }

        } catch (Exception e) {
            return Observable.error(e);
        }
        return invokeResult;
    }

    private Object [] deserializeParameter(Method serviceMethod, JsonArray payload) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?>[] parameterTypes = serviceMethod.getParameterTypes();

        if(parameterTypes.length == 0) {
            return null;
        }

        Object[] args = new Object[payload.size()];

        if(parameterTypes.length != payload.size()) {
            throw new RuntimeException("Wrong Parameter Count");
        }

        int i = 0;
        for(Class<?> parameterType: parameterTypes) {
            if(Jsonizable.class.isAssignableFrom(parameterType)) {
                args[i] = parameterType.getConstructor(JsonObject.class).newInstance(payload.getValue(i));
            } else if(String.class.isAssignableFrom(parameterType)) {
                args[i] = payload.getString(i);
            } else if(Long.class.isAssignableFrom(parameterType)) {
                args[i] = payload.getLong(i);
            } else if(Integer.class.isAssignableFrom(parameterType)) {
                args[i] = payload.getInteger(i);
            } else if(Boolean.class.isAssignableFrom(parameterType)) {
                args[i] = payload.getBoolean(i);
            } else if(Double.class.isAssignableFrom(parameterType)) {
                args[i] = payload.getDouble(i);
            }  else if(JsonObject.class.isAssignableFrom(parameterType)) {
                args[i] = payload.getJsonObject(i);
            } else if(JsonArray.class.isAssignableFrom(parameterType)) {
                args[i] = payload.getJsonArray(i);
            } else {
                throw new RuntimeException("Parameter Type does not implement Jsonizable");
            }

            i++;
        }

        return args;
    }

    private void setReplyHandler(Message<Object> message, Method serviceMethod, Observable<Object> serviceResult) {
        if (null == serviceResult) {
            message.reply(null);
        } else {
            serviceResult
                    .map(response -> serializeResponse(response))
                    .doOnNext(System.out::println)
                    .doOnError(System.out::println)
                    .subscribe(
                            message::reply,
                            throwable -> message.fail(1, "Error invoking abstractService method " + serviceMethod + "(Cause: " + throwable.getMessage() + ")"),
                            () -> message.reply(null)
                    );
        }
    }

    private Object serializeResponse(Object response){
        if(response instanceof Jsonizable) {
            return ((Jsonizable) response).toJson();
        } else if(response instanceof JsonObject) {
            return response;
        } else if(response instanceof JsonArray) {
            return response;
        } else {
            return new JsonObject().put("result", response);
        }

    }

    private String getActionNameFromMessageHeader(Message<Object> message) {
        MultiMap headers = message.headers();
        if (!headers.contains(ACTION_HEADER)) {
            throw new IllegalStateException("Action header " + ACTION_HEADER + " not set for message " + message);
        }
        return headers.get("action");
    }
}
