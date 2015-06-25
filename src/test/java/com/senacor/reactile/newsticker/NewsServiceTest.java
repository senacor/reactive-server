package com.senacor.reactile.newsticker;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import rx.observers.TestSubscriber;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class NewsServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.NewsService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.newsticker.NewsService service;

    @Test
    @Ignore
    public void testGettingSingleNews() {
        News first = service.getNewsObservable().toBlocking().first();

        assertThat(first, is(notNullValue()));
    }

    @Test
    @Ignore
    public void testGettingSeveralNews() {
        TestSubscriber<News> subscriber = new TestSubscriber<News>();

        service.getNewsObservable()
                .take(3)
                .subscribe(subscriber);

        subscriber.awaitTerminalEvent();


    }

}
