package com.senacor.reactile.service.newsticker;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class NewsTickerStreamSubject {

    public static final int MAX_LATEST_NEWS_PER_PAGE = 25;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Subject<NewsCollection, NewsCollection> newsSubject;

    private final NewsTickerStream newsTickerStream;

    @Inject
    public NewsTickerStreamSubject(NewsTickerStream newsTickerStream) {
        this.newsTickerStream = newsTickerStream;

        newsSubject = BehaviorSubject.create();

        Observable<NewsCollection> observable = newsTickerStream.getNewsObservable()
                .buffer(250, TimeUnit.MILLISECONDS, MAX_LATEST_NEWS_PER_PAGE)
                .map(NewsCollection::new)
                .doOnNext(newsSubject::onNext);

        observable.subscribe(newsSubject);

        newsSubject.subscribe(col -> log.info("NewsTicker streaming NewsCollection received: " + col.toString()),
                ex -> log.error("NewsTicker streaming failed and terminated.", ex),
                () -> log.info("NewsTicker streaming completed."));
    }

    public Subject<NewsCollection, NewsCollection> getNewsSubject() {
        return newsSubject;
    }

}
