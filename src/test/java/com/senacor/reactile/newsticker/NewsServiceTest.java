package com.senacor.reactile.newsticker;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerFixtures;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.observers.TestSubscriber;

import javax.inject.Inject;

import java.util.List;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NewsServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.NewsService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.newsticker.NewsService service;
    @Inject
    private EventBus eventBus;

    @Test
    public void thatCustomerIsReturned() throws InterruptedException {

        TestSubscriber<News> ts = new TestSubscriber<News>();

        vertxRule.vertx().eventBus()
                .consumer(NewsServiceVerticle.ADDRESS)
                .toObservable()
                .take(10)
                .map(Message::body)
                .cast(JsonObject.class)
                .map(News::fromJson)
                .subscribe(ts);

        ts.awaitTerminalEvent();

        NewsCollection newsCollection = service.getLatestNewsObservable(10).toBlocking().first();
        assertThat(newsCollection.getNews(), Matchers.hasSize(10));
    }

}