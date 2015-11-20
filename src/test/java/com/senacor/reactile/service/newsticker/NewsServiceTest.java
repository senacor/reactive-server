package com.senacor.reactile.service.newsticker;

import static com.senacor.reactile.service.newsticker.NewsTickerStream.INTERVAL_WAIT_TIME;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

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
            Thread.sleep(INTERVAL_WAIT_TIME * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final NewsCollection latestNews = newsService.getLatestNews(10).toBlocking().first();

        assertThat(latestNews.getNews()).hasSize(10);
    }

    @Test
    public void testThatNewesetNewsIsRetrieved() {
        try {
            Thread.sleep(INTERVAL_WAIT_TIME * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final List<String> newsBeforeWait =
            newsService.getLatestNews(2).toBlocking().first().getNews().stream().map(News::getTitle).collect(Collectors.toList());

        try {
            Thread.sleep(INTERVAL_WAIT_TIME * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final List<String> newsAfterWait =
            newsService.getLatestNews(2).toBlocking().first().getNews().stream().map(News::getTitle).collect(Collectors.toList());

        assertThat(newsBeforeWait).doesNotContainAnyElementsOf(newsAfterWait);
    }
}