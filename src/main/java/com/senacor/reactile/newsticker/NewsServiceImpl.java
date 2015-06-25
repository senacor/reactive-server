package com.senacor.reactile.newsticker;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;

import javax.inject.Inject;

public class NewsServiceImpl implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsServiceImpl.class);

    private final Vertx vertx;
    private NewsTickerStream newsTickerStream = new NewsTickerStream();

    @Inject
    public NewsServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void getNews(Handler<AsyncResult<News>> resultHandler) {
        Rx.bridgeHandler(newsTickerStream.getNewsObservable(), resultHandler);
    }
}
