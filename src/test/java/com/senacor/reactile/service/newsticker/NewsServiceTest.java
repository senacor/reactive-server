package com.senacor.reactile.service.newsticker;

import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class NewsServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.NewsService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private NewsService newsService;

    @Test
    public void thatNewsItemsAreDelivered() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final NewsCollection latestNews = newsService.getLatestNews(10).toBlocking().first();

        Assertions.assertThat(latestNews.getNews()).hasSize(10);
    }

    // TODO Mehr Tests schreiben
}