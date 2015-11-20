package com.senacor.reactile.service.newsticker;

import com.google.inject.Inject;
import org.apache.commons.collections.buffer.BoundedFifoBuffer;
import rx.Observable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hannes on 19/11/15.
 */
public class NewsServiceImpl implements NewsService {

    private final NewsTickerStream newsTickerStream;

    private BoundedFifoBuffer latestNewsQueue = new BoundedFifoBuffer(20);


    @Inject
    public NewsServiceImpl(NewsTickerStream newsTickerStream) {
        this.newsTickerStream = newsTickerStream;
        newsTickerStream.getNewsObservable().subscribe(latestNewsQueue::add);
    }

    @Override
    public Observable<NewsCollection> getLatestNews(Integer max) {
        List<News> latestNews = new ArrayList<>();
        Iterator iterator = latestNewsQueue.iterator();
        while (iterator.hasNext()) {
            News next = (News) iterator.next();
            latestNews.add(next);
        }
        return Observable.just(new NewsCollection(latestNews));
    }

    @Override
    public Observable<News> streamNews() {
        return newsTickerStream.getNewsObservable();
    }
}
