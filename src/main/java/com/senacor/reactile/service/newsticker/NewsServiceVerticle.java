package com.senacor.reactile.service.newsticker;

import javax.inject.Inject;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;


public class NewsServiceVerticle extends AbstractServiceVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final NewsService newsService;

    @Inject
    public NewsServiceVerticle(@Impl NewsService newsService) {
        super(newsService);
        this.newsService = newsService;
    }

}
