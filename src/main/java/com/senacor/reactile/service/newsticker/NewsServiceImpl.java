package com.senacor.reactile.service.newsticker;

import com.google.inject.Inject;
import com.senacor.reactile.json.JsonObjects;
import io.vertx.rxjava.core.Vertx;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import rx.Observable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hannes on 19/11/15.
 */
public class NewsServiceImpl implements NewsService {

    private final NewsTickerStream newsTickerStream;

    private CircularFifoBuffer latestNewsQueue = new CircularFifoBuffer(20);

    private final Vertx vertx;


    @Inject
    public NewsServiceImpl(NewsTickerStream newsTickerStream, Vertx vertx) {
        this.newsTickerStream = newsTickerStream;
        this.vertx = vertx;
        newsTickerStream.getNewsObservable().subscribe((element) -> {
            latestNewsQueue.add(element);
            vertx.eventBus().publish(NEWS_STREAM, JsonObjects.toJson(element));
        });
    }

    @Override
    public Observable<NewsCollection> getLatestNews(Integer max) {
        List<News> latestNews = new ArrayList<>();
        Iterator iterator = latestNewsQueue.iterator();
        int count = 0;
        while (iterator.hasNext() && count < max) {
            News next = (News) iterator.next();
            latestNews.add(next);
            count++;
        }
        return Observable.just(new NewsCollection(latestNews));
    }

    @Override
    public Observable<News> streamNews() {
        return newsTickerStream.getNewsObservable();
    }
}
