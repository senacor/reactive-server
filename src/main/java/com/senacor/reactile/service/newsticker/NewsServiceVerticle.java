package com.senacor.reactile.service.newsticker;

import com.google.inject.Inject;
import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class NewsServiceVerticle extends AbstractServiceVerticle {

    @Inject
    public NewsServiceVerticle(@Impl NewsService serviceInstance) {
        super(serviceInstance);
    }
}
