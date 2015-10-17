package com.senacor.reactile.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

public class Callbacks {

    public static <T, A> Handler<AsyncResult<A>> callback(Handler<AsyncResult<T>> resultHandler, Function<A, T> function) {
        return result -> {
            if (result.failed()) {
                resultHandler.handle(failedFuture(result.cause()));
            } else {
                resultHandler.handle(succeededFuture(function.apply(result.result())));
            }
        };
    }


    public static <T, A> Handler<AsyncResult<List<A>>> listCallback(Handler<AsyncResult<List<T>>> resultHandler, Function<A, T> function) {
        return result -> {
            if (result.failed()) {
                resultHandler.handle(failedFuture(result.cause()));
            } else {

                List<T> resList = result.result().stream().<T>map(element -> function.apply(element)).collect(Collectors.toList());
                Future<List<T>> future = io.vertx.core.Future.succeededFuture(resList);
                resultHandler.handle(future);
            }
        };
    }
}
