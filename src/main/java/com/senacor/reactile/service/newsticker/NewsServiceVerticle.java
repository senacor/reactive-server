package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.service.customer.CustomerService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import javax.inject.Inject;


public class NewsServiceVerticle extends AbstractServiceVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final NewsService newsService;

    @Inject
    public NewsServiceVerticle(@Impl NewsService newsService) {
        super(newsService);
        this.newsService = newsService;
    }

}
