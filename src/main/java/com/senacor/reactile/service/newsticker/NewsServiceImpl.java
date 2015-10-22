package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class NewsServiceImpl implements NewsService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final NewsTickerStream newsTickerStream;

    private final int MAX_VALUES_IN_MEMORY = 100;
    private List<News> newsList = new ArrayList<News>(MAX_VALUES_IN_MEMORY + 1);

    @Inject
    public NewsServiceImpl(NewsTickerStream newsTickerStream) {
        this.newsTickerStream = newsTickerStream;

        newsTickerStream.getNewsObservable().subscribe(
                newsItem -> {
                    newsList.add(newsItem);

                    if (newsList.size() > MAX_VALUES_IN_MEMORY) {
                        newsList.remove(0);
                    }
                },
                Throwable::printStackTrace,
                () -> System.out.println("erzeugeEinObservable Completed.")
        );
    }

    @Override
    public void getLatestNews(int max, Handler<AsyncResult<NewsCollection>> resultHandler) {
        int lowIdx = 0;

        if (newsList.size() - max > 0) {
            lowIdx = newsList.size() - max;
        }

        Rx.bridgeHandler(
                Observable.just(new NewsCollection(newsList.subList(lowIdx, newsList.size()))),
                resultHandler);
    }

}
