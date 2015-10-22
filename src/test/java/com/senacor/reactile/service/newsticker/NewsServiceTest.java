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
import java.util.List;

import static org.junit.Assert.assertEquals;

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
        final int max = 1;

        Thread.sleep(150L);

        List<News> news = service.getLatestNewsObservable(max)
                .map(collection -> collection.getNews())
                .toBlocking().first();


        assertEquals(1, news.size());
        //newsCollections.stream().forEach(newsCollection -> assertTrue(newsCollection.getNews().size() <= max));
    }

}
