package com.senacor.reactile.service.newsticker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;
import rx.schedulers.Schedulers;

public class NewsServiceImpl implements NewsService{
    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    private final NewsTickerStream newsTickerStream;
    private final Vertx vertx;
    private final CircularFifoQueue<News> newFifQueue;

    @Inject
    public NewsServiceImpl(Vertx vertx) {
        this.newsTickerStream = new NewsTickerStream();
        this.vertx = vertx;
        this.newFifQueue = new CircularFifoQueue<News>(10);

        Observable<News> o= newsTickerStream.getNewsObservable().subscribeOn(Schedulers.io())
                .doOnEach(news -> {
                    newFifQueue.add((News)news.getValue());
                })
                .doOnError(throwable -> {
                    logger.error("error" + throwable.getMessage());
                });

		o.subscribe(next -> {
			System.out.println("############################## queue:" + newFifQueue);

			String eventAddress = NewsService.ADDRESS;
			logger.info("publishing on '" + eventAddress + "'...");

			vertx.eventBus()
					.publish(eventAddress,
							NewsChangedEvt.newBuilder().withId(UUID.randomUUID().toString())
									.withNewsCollection(
											new NewsCollection(Arrays.asList(newFifQueue.toArray(new News[10]))))
					.build().toJson());
			logger.info("publishing on '" + eventAddress + "' done");

		});
        
    }

    @Override
	public Observable<NewsCollection> getLatestNews(Integer max) {
        List<News> listOfNews = new ArrayList<>();
        while ((listOfNews.size() < max) && (!newFifQueue.isEmpty())) {
            listOfNews.add(newFifQueue.peek());
        }

        NewsCollection  newsCollection = new NewsCollection(listOfNews);
        return Observable.defer(()-> {return Observable.just(newsCollection);});

	}


}
