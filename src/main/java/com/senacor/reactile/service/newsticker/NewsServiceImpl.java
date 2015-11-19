package com.senacor.reactile.service.newsticker;

import java.util.Queue;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;
import rx.internal.util.SynchronizedQueue;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class NewsServiceImpl implements NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    public static final int QUEUE_SIZE = 100;

    private Queue<News> newsQueue;

    @Inject
    public NewsServiceImpl(Vertx vertx, NewsTickerStream newsTickerStream) {
        newsQueue = new SynchronizedQueue<>(QUEUE_SIZE);

        newsTickerStream.getNewsObservable() //
            .doOnNext(newsItem -> {
                String eventAddress = NewsService.ADDRESS;
                logger.info("creating or updating on '" + eventAddress + "'...");
                vertx.eventBus().publish( //
                    eventAddress, //
                    NewsItemsUpdatedEvent //
                        .newBuilder() //
                        .withId(newsItem.getTitle()) //
                        .withNews(newsItem) //
                        .build() //
                        .toJson());
                logger.info("creating or updating on '" + eventAddress + "' done");
            }) //
            .subscribe(newsQueue::add, Throwable::printStackTrace);
    }

    @Override
    public Observable<NewsCollection> getLatestNews(Integer max) {
        return Observable //
            .just(max) //
            .map(length -> newsQueue.stream().limit(length).collect(Collectors.toList()))
            .map(NewsCollection::new);
    }
}
