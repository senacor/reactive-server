package com.senacor.reactile.rx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.rx.java.RxHelper;
import rx.Observable;
import rx.Subscriber;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;


public final class Rx {

    private Rx() {
    }

    public static <T> void bridgeHandler(Observable<T> observable, Handler<AsyncResult<T>> handler) {
        observable.subscribe(
                res -> handler.handle(succeededFuture(res)),
                throwable -> {
                    throwable.printStackTrace();
                    handler.handle(failedFuture(throwable));
                }
        );
    }

    public static <T> void bridgeHandler(Observable<T> observable, Handler<AsyncResult<T>> handler, Vertx scheduler) {
        observable
                .subscribeOn(RxHelper.scheduler(scheduler))
                .subscribe(
                        res -> handler.handle(succeededFuture(res)),
                        throwable -> handler.handle(failedFuture(throwable))
                );
    }

    public static <T> Subscriber<T> toSubscriber(Handler<AsyncResult<T>> resultHandler) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                resultHandler.handle(failedFuture(throwable));
            }

            @Override
            public void onNext(T t) {
                resultHandler.handle(succeededFuture(t));
            }
        };
    }

}
