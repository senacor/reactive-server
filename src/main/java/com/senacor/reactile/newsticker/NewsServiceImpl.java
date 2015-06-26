package com.senacor.reactile.newsticker;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NewsServiceImpl implements NewsService {

    private final EventBus eventBus;

    private final Set<News> newsList;

    @Inject
    public NewsServiceImpl(EventBus eventBus) {
        this.eventBus = eventBus;
        this.newsList = Collections.newSetFromMap(
                new LinkedHashMap<News, Boolean>(32, 0.7f, true) {
                    protected boolean removeEldestEntry(
                            Map.Entry<News, Boolean> eldest) {
                        return size() > 100;
                    }
                }
        );
    }

    @PostConstruct
    private void init() {
        eventBus.consumer(NewsServiceVerticle.ADDRESS).toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .map(News::fromJson)
                .subscribe(newsList::add);
    }

    @Override
    public void getLatestNews(int max, Handler<AsyncResult<NewsCollection>> resultHandler) {
        Observable<NewsCollection> observable =
                Observable.from(newsList)
                .take(Math.min(newsList.size(), max))
                .toList()
                .map(NewsCollection::new);
        Rx.bridgeHandler(observable, resultHandler);
    }

}
