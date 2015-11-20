package com.senacor.reactile.service.news;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.service.newsticker.NewsCollection;
import com.senacor.reactile.service.newsticker.NewsService;

public class NewsServiceTest {

	@ClassRule
	public final static VertxRule vertxRule = new VertxRule(Services.NewsService);

	@Rule
	public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

	@Inject
	private NewsService service;

	@Test
	public void thatLastNewsAreReturned() throws InterruptedException {
	
		Thread.sleep(1000);
		
		NewsCollection result = service.getLatestNews(10).toBlocking().first();
		assertThat(result.getNews().size(), equalTo(10));

	}



}