package com.senacor.reactile.newsticker;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
                .toObservable()
                .take(3)
                .subscribe(ts);

        ts.awaitTerminalEvent();
        List<Message<Object>> messages = ts.getOnNextEvents();

        assertThat(messages, hasSize(3));

        for (Message<Object> message : messages) {
            // TODO: get rid of the cast?
            JsonObject body = (JsonObject) message.body();
            assertThat(body.getString("title"), is(notEmptyString()));
            assertThat(body.getString("news"), is(notEmptyString()));
        }
    }

    private static Matcher<String> notEmptyString() {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String string) {
                return string != null && string.length() > 0;
            }

            @Override
            public void describeTo(Description description) {}
        };
    }

}