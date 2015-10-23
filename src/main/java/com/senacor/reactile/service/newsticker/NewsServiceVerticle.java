package com.senacor.reactile.service.newsticker;

import static com.senacor.reactile.service.newsticker.NewsService.ADDRESS_NEWS_STREAM;

import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;


public class NewsServiceVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final NewsService newsService;
    private final NewsTickerStream newsTickerStream;

    @Inject
    public NewsServiceVerticle(@Impl NewsService newsService, NewsTickerStream newsTickerStream) {
        this.newsService = newsService;
        this.newsTickerStream = newsTickerStream;
    }

    @Override
    public void start() throws Exception {
        log.info("Starting service Verticle: " + config().getString("address"));
        String address = config().getString("address");
        if (address == null) {
            throw new IllegalStateException("address field must be specified in config for NewsService");
        }
        ProxyHelper.registerService(NewsService.class, getVertx(), newsService, address);

        pushNews();
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping service Verticle: " + config().getString("address"));
    }

    public void pushNews() {
        newsTickerStream.getNewsObservable().subscribe(
                news -> getVertx().eventBus().publish(ADDRESS_NEWS_STREAM, news.toJson())
        );
    }

}
