package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import javax.inject.Inject;

public class NewsServiceVerticle extends AbstractServiceVerticle {
    @Inject
    public NewsServiceVerticle(@Impl NewsService newsService) {
        super(newsService);
    }

}