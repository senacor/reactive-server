package com.senacor.reactile.service.newsticker;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NewsServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.NewsService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);


    @Inject
    private NewsService newsService;

    @Test
    public void shouldReturnNews() {
        NewsCollection first = newsService.getLatestNews(10).toBlocking().first();

        assertEquals(10, first.getNews().size());
    }


}