package com.senacor.reactile.newsticker;

import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;

import javax.inject.Inject;


public class NewsServiceVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String ADDRESS = "foobar123";

    private final NewsService newsService;

    private final NewsTickerStream newsTickerStream;

    @Inject
    public NewsServiceVerticle(@Impl NewsService newsService) {
        this.newsService = newsService;

        newsTickerStream = new NewsTickerStream();
    }

    @Override
    public void start() throws Exception {
        newsTickerStream.getNewsObservable().subscribe(news -> {
            vertx.eventBus().publish(ADDRESS, news.toJson());
        });
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping service Verticle: " + config().getString("address"));
    }

}
