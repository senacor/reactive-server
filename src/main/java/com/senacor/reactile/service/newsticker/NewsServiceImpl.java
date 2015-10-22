package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import javax.inject.Inject;

public class NewsServiceImpl implements NewsService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final NewsTickerStream newsTickerStream;

    @Inject
    public NewsServiceImpl(NewsTickerStream newsTickerStream) {
        this.newsTickerStream = newsTickerStream;
    }

    @Override
    public void getLatestNews(int max, Handler<AsyncResult<NewsCollection>> resultHandler) {
        Rx.bridgeHandler(newsTickerStream.getNewsObservable()
                        .takeLastBuffer(max)
                        .map(NewsCollection::new)
                        .doOnNext(log::info),
                resultHandler);
    }

}
