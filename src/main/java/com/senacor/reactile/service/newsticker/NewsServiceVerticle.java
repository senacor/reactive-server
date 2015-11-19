package com.senacor.reactile.service.newsticker;

import com.google.inject.Inject;
import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;

/**
 * Created by hannes on 19/11/15.
 */
public class NewsServiceVerticle extends AbstractServiceVerticle {

    @Inject
    public NewsServiceVerticle(@Impl NewsService serviceInstance) {
        super(serviceInstance);
    }
}
