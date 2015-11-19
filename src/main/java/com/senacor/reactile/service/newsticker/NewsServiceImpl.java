package com.senacor.reactile.service.newsticker;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NewsServiceImpl implements NewsService{
    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    private final NewsTickerStream newsTickerStream;
    private final Vertx vertx;
    private final CircularFifoQueue<News> newFifQueue;

    public NewsServiceImpl(NewsTickerStream newsTickerStream, Vertx vertx) {
        this.newsTickerStream = newsTickerStream;
        this.vertx = vertx;
        this.newFifQueue = new CircularFifoQueue<News>(10);

        newsTickerStream.getNewsObservable().subscribeOn(Schedulers.io())
                .doOnEach(news -> {
                    newFifQueue.add(news.getValue());
                })
                .doOnError(throwable -> {
                    logger.error("error" + throwable.getMessage());
                });
    }

    @Override
	public Observable<NewsCollection> getLatestNews(int max) {
        List<News> listOfNews = new ArrayList<>();
        while ((listOfNews.size() < max) && (!newFifQueue.isEmpty())) {
            listOfNews.add(newFifQueue.peek());
        }

        NewsCollection  newsCollection = new NewsCollection(listOfNews);
        return Observable.defer(()-> {return Observable.just(newsCollection);});

	}


}
