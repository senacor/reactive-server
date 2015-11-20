package com.senacor.reactile.service.newsticker;

import com.google.inject.Inject;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import rx.Observable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hannes on 19/11/15.
 */
public class NewsServiceImpl implements NewsService {

    private final NewsTickerStream newsTickerStream;

    private CircularFifoBuffer latestNewsQueue = new CircularFifoBuffer(20);


    @Inject
    public NewsServiceImpl(NewsTickerStream newsTickerStream) {
        this.newsTickerStream = newsTickerStream;
        newsTickerStream.getNewsObservable().subscribe(latestNewsQueue::add);
    }

    @Override
    public Observable<NewsCollection> getLatestNews(Integer max) {
        List<News> latestNews = new ArrayList<>();
        Iterator iterator = latestNewsQueue.iterator();
        int count = 0;
        while (iterator.hasNext() && count < max) {
            News next = (News) iterator.next();
            latestNews.add(next);
            count++;
        }
        return Observable.just(new NewsCollection(latestNews));
    }

    @Override
    public Observable<News> streamNews() {
        return newsTickerStream.getNewsObservable();
    }
}
