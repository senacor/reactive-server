package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NewsServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.NewsService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.service.newsticker.NewsService service;

    @Test
    public void thatNewsAreReturned() throws InterruptedException {
        final int max = 10;

        final List<NewsCollection> newsCollections = new ArrayList<>();

        service.getLatestNews(max, list -> newsCollections.add(list.result()));

        Thread.sleep(1000L);

        assertEquals(1, newsCollections.size());
        newsCollections.stream().forEach(newsCollection -> assertTrue(newsCollection.getNews().size() <= max));
    }

}
