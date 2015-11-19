package com.senacor.reactile.service.newsticker;

import rx.Observable;

public class NewsServiceImpl implements NewsService {
    private final NewsTickerStream newsTickerStream = new NewsTickerStream();

    @Override
    public Observable<NewsCollection> getLatestNews(Integer max) {
        return newsTickerStream.getNewsObservable().buffer(max).map(NewsCollection::new);
    }
}
