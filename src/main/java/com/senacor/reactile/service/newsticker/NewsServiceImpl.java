package com.senacor.reactile.service.newsticker;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import javax.inject.Inject;

public class NewsServiceImpl implements NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    @Inject
    private NewsTickerStream newsTickerStream;


    private final Vertx vertx;

    @Inject
    public NewsServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<NewsCollection> getLatestNews(Integer max) {
        return newsTickerStream.getNewsObservable()
                .buffer(max)
                .map(NewsCollection::new);
    }
}
