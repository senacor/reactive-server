package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.service.branch.BranchService;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by hannes on 19/11/15.
 */
public class NewsServiceTest {
    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.NewsService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private NewsService service;


    @Test
    public void testLatest10News() {
        final NewsCollection newsCollection = service.getLatestNews(10).toBlocking().first();
        System.out.println("newsCollection.getNews() = " + newsCollection.getNews());
        assertEquals(10, newsCollection.getNews().size());
    }

    @Test
    public void testStreamingNews() throws InterruptedException {
        final List<News> latestNews = new ArrayList<>();

        service.streamNews().subscribe(latestNews::add);

        while (latestNews.size() < 10) {
            Thread.sleep(100);
        }
        assertEquals(10, latestNews.size());
    }


}
