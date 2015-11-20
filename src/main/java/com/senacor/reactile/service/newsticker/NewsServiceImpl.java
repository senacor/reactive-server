package com.senacor.reactile.service.newsticker;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class NewsServiceImpl implements NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    public static final int QUEUE_SIZE = 100;

    private Deque<News> newsQueue;

    @Inject
    public NewsServiceImpl(Vertx vertx, NewsTickerStream newsTickerStream) {
        newsQueue = new LinkedList<>();

        newsTickerStream.getNewsObservable() //
            .doOnNext(newsItem -> {
                String eventAddress = NewsService.PUBLISH_ADDRESS_NEWS;
                logger.debug("creating or updating on '" + eventAddress + "'...");
                vertx.eventBus().publish( //
                    eventAddress, //
                    NewsItemsUpdatedEvent //
                        .newBuilder() //
                        .withId(newsItem.getTitle()) //
                        .withNews(newsItem) //
                        .build() //
                        .toJson());
                logger.debug("creating or updating on '" + eventAddress + "' done");
            }) //
            .subscribe(newsItem -> {
                newsQueue.push(newsItem);
                if(newsQueue.size() >= QUEUE_SIZE) {
                    newsQueue.removeLast();
                }
            }, Throwable::printStackTrace);
    }

    @Override
    public Observable<NewsCollection> getLatestNews(Integer max) {
        return Observable //
            .just(max) //
            .map(length -> newsQueue.stream().limit(length).collect(Collectors.toList()))
            .map(NewsCollection::new);
    }
}
