package com.senacor.reactile.service.newsticker;

import com.google.inject.Inject;
import rx.Observable;

/**
 * Created by hannes on 19/11/15.
 */
public class NewsServiceImpl implements NewsService {

    private final NewsTickerStream newsTickerStream;

    @Inject
    public NewsServiceImpl(NewsTickerStream newsTickerStream) {
        this.newsTickerStream = newsTickerStream;
    }

    @Override
    public Observable<NewsCollection> getLatestNews(Integer max) {
        // TODO replace by hot observable!
        return newsTickerStream.getNewsObservable().take(max).buffer(max).flatMap(newses -> Observable.just(new NewsCollection(newses)));
    }

    @Override
    public Observable<News> streamNews() {
        return newsTickerStream.getNewsObservable();
    }
}
