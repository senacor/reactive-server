package com.senacor.reactile.newsticker;

import javax.inject.Inject;

import com.senacor.reactile.guice.Impl;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;
import rx.Subscription;


public class NewsServiceVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String ADDRESS = "NewsTicker";

    private final NewsTickerStream newsTickerStream;

    private final NewsService newsService;

    private Subscription subscription;

    @Inject
    public NewsServiceVerticle(@Impl NewsService newsService) {

        newsTickerStream = new NewsTickerStream();
        this.newsService = newsService;
    }


    @Override
    public void start() throws Exception {
        subscription = newsTickerStream.getNewsObservable().subscribe(news -> {
            vertx.eventBus().publish(ADDRESS, news.toJson());
        });

        log.info("Starting service: " + config().getString("main"));
        String address = config().getString("address");
        if (address == null) {
            throw new IllegalStateException("address field must be specified in config for NewsService");
        }
        ProxyHelper.registerService(NewsService.class, getVertx(), newsService, address);
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping service Verticle: " + config().getString("address"));
        subscription.unsubscribe();
    }

}
