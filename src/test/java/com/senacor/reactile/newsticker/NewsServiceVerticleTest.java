package com.senacor.reactile.newsticker;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class NewsServiceVerticleTest {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceVerticleTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.NewsService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Test
    public void thatUserCanBeObtainedFromDatabase() throws ExecutionException, InterruptedException, TimeoutException {
        TestSubscriber<Message<Object>> ts = new TestSubscriber<Message<Object>>();
        vertxRule.vertx().eventBus()
                .consumer(NewsServiceVerticle.ADDRESS)
                .toObservable().subscribe(ts);

        logger.warn("FOOBAR");
        ts.awaitTerminalEvent();
    }
}