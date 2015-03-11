package com.senacor.reactile.mongo;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

import static rx.Observable.just;

public class MongoInitializer {

    private final ObservableMongoService mongoService;
    private final String collection;

    public MongoInitializer(ObservableMongoService mongoService, String collection) {
        this.mongoService = mongoService;
        this.collection = collection;
    }

    public MongoInitializer(Vertx vertx, String collection) {
        this(ObservableMongoService.from(vertx), collection);
    }

    public List<String> writeBlocking(Jsonizable object, Jsonizable... objects) {
        List<String> ids = new ArrayList<>();
        write(object, objects)
                .toBlocking()
                .forEach(ids::add);
        return ids;
    }

    public Observable<String> write(Jsonizable object, Jsonizable... objects) {
        return Observable.from(objects)
                .concatWith(just(object))
                .map(o -> o.toJson())
                .flatMap(json -> mongoService.insert(collection, json));
    }


}
