package com.senacor.reactile.mongo;

import com.senacor.reactile.domain.IdObject;
import com.senacor.reactile.domain.Identity;
import com.senacor.reactile.json.Jsonizable;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoService;
import org.junit.rules.ExternalResource;
import rx.Observable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;

public class MongoInitializer extends ExternalResource{

    private final MongoService mongoService;
    private final String collection;

    public MongoInitializer(MongoService mongoService, String collection) {
        this.mongoService = mongoService;
        this.collection = collection;
    }

    public MongoInitializer(Vertx vertx, String collection) {
        this(new MongoService(io.vertx.ext.mongo.MongoService.createEventBusProxy((io.vertx.core.Vertx) vertx.getDelegate(), "vertx.mongo")), collection);
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
                .map(o ->
                        o instanceof IdObject ? o.toJson().put("_id", ((IdObject) o).toValue()) :
                                o instanceof Identity ? o.toJson().put("_id", ((Identity) o).getId().toValue()) :
                                        o.toJson())
                .flatMap(json -> mongoService.insertObservable(collection, json));
    }

    @Override
    protected void after() {
        mongoService.dropCollectionObservable(collection).subscribe();
    }
}
