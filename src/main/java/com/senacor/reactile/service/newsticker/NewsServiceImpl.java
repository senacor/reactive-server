package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class NewsServiceImpl implements NewsService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final NewsTickerStreamSubject newsTickerStreamSubject;

    @Inject
    public NewsServiceImpl(NewsTickerStreamSubject newsTickerStreamSubject) {
        this.newsTickerStreamSubject = newsTickerStreamSubject;
    }

    @Override
    public void getLatestNews(int max, Handler<AsyncResult<NewsCollection>> resultHandler) {
        if (!(0 < max && max < NewsTickerStreamSubject.MAX_LATEST_NEWS_PER_PAGE)) {
            throw new IllegalArgumentException("Parameter max must be between 1 and " + NewsTickerStreamSubject.MAX_LATEST_NEWS_PER_PAGE + "!");

        }

        Rx.bridgeHandler(newsTickerStreamSubject.getNewsSubject()
                .flatMap(col -> Observable.from(col.getNews()))
                .buffer(20, TimeUnit.MILLISECONDS, max)
                .map(NewsCollection::new)
                , resultHandler);
    }

}
