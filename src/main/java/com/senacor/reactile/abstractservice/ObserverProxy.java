package com.senacor.reactile.abstractservice;

import com.senacor.reactile.json.Jsonizable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.ObservableFuture;
import rx.Observable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

/**
 * Created by mmenzel on 25.06.2015.
 */
public class ObserverProxy implements InvocationHandler{

    private Vertx vertx;
    private String address;

    public ObserverProxy(Vertx vertx, String address) {
        this.vertx = vertx;
        this.address = address;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().startsWith("add")) {
            return false;
        }

        Action.MessagePattern pattern = getMessagePatternFromMethodAction(method);

        Observable<Object> resultFuture = null;

        if(pattern.equals(Action.MessagePattern.RequestResponse)){
            resultFuture = sendObservable(method, args);
        } else if(pattern.equals(Action.MessagePattern.PublishSubsrcribe)){
            resultFuture = consumeObservable(method, args);
        }

        return resultFuture;
    }

    private Action.MessagePattern getMessagePatternFromMethodAction(Method method) {
        Action action = method.getAnnotation(Action.class);
        if(action==null) {
            throw new RuntimeException("Action annotation not found for method " + method.getName());
        }
        return action.pattern();
    }

    private Observable<Object> consumeObservable(Method method, Object[] args) {

        return Observable.create(observable -> {
            vertx.eventBus().consumer(address+"."+method.getName(), res -> {
                try {
                    observable.onNext(deserializeResultFromJson(method, res));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e){
                    observable.onError(e);
                }
            });
        });
    }

    private ObservableFuture<Object> sendObservable(Method method, Object[] args) {

        JsonArray jsonArray = serializeArguments(args);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.addHeader("action", method.getName());
        ObservableFuture<Object> resultFuture = io.vertx.rx.java.RxHelper.observableFuture();
        Handler<AsyncResult<Object>> resultHandler = resultFuture.toHandler();

        vertx.eventBus().<Object>send(address, jsonArray, deliveryOptions, res -> {
            //der Handler ist vom Typ Handler<AsyncResult<Message<Object>>
            //Die Message muss hier erstmal ausgepakt werden und in das resultfuture verpakt werden
            //Optimierung: Die direkte Erzeugung eines Observable über rx-vertx. Problem 3.0 Alpha-Version: Vertx wurde nicht in der rx-Version injected
            if (res.failed()) {
                resultHandler.handle(failedFuture(res.cause()));
            } else {
                try {
                    Object result = deserializeResultFromJson(method, res.result());
                    resultHandler.handle(succeededFuture(result));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e){
                    resultHandler.handle(failedFuture(e.getMessage()));
                }
            }
        });
        return resultFuture;
    }

    private Object deserializeResultFromJson(Method method, Message<Object> res) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        JsonObject jsonResult = (JsonObject)res.body();

        Class<?> type = method.getAnnotation(Action.class).returnType();

        if(Jsonizable.class.isAssignableFrom(type)) {
            return type.getConstructor(JsonObject.class).newInstance(jsonResult);
        } else if (String.class.isAssignableFrom(type)){
            return jsonResult.getString("result");
        } else  if(Long.class.isAssignableFrom(type)) {
            return jsonResult.getLong("result");
        } else if(Integer.class.isAssignableFrom(type)) {
            return jsonResult.getInteger("result");
        } else if(Boolean.class.isAssignableFrom(type)) {
            return jsonResult.getBoolean("result");
        } else if(Double.class.isAssignableFrom(type)) {
            return jsonResult.getDouble("result");
        } else if(JsonObject.class.isAssignableFrom(type)) {
            return jsonResult;
        } else if(JsonArray.class.isAssignableFrom(type)) {
            return jsonResult;
        } else {
            throw new RuntimeException("Return Type does not implement Jsonizable");
        }
    }

    private JsonArray serializeArguments(Object[] args) {
        JsonArray jsonArray = new JsonArray();

        if(args!=null) {
            for (Object arg : args) {
                if (arg instanceof Jsonizable) {
                    jsonArray.add(((Jsonizable) arg).toJson());
                } else {
                    jsonArray.add(arg);
                }
            }
        }
        return jsonArray;
    }
}