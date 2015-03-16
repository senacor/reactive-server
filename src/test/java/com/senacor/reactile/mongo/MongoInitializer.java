package com.senacor.reactile.mongo;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.rxjava.core.Vertx;
import org.junit.rules.ExternalResource;
import rx.Observable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;

public class MongoInitializer extends ExternalResource{

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
        LinkedList<Jsonizable> list = newLinkedList(newArrayList(objects));
        list.add(0, object);
        return writeBlocking(list);
    }

    public List<String> writeBlocking(Iterable<? extends Jsonizable> objects) {
        List<String> ids = new ArrayList<>();
        write(objects)
                .toBlocking()
                .forEach(ids::add);
        return ids;
    }

    public Observable<String> write(Iterable<? extends Jsonizable> objects) {
        return Observable.from(objects)
                .map(o -> o.toJson())
                .flatMap(json -> mongoService.insert(collection, json));
    }

    @Override
    protected void after() {
        mongoService.dropCollection(collection).subscribe();
    }
}
