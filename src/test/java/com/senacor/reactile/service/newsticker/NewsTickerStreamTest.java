package com.senacor.reactile.service.newsticker;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.Test;
import rx.Subscription;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertNotNull;

/**
 * @author Andreas Keefer
 */
public class NewsTickerStreamTest {

    private static final Logger logger = LoggerFactory.getLogger(NewsTickerStreamTest.class);

    @Test
    public void testNewsStream() throws Exception {
        News firstnEWS = new NewsTickerStream().getNewsObservable()
                .toBlocking()
                .first();
        assertNotNull(firstnEWS.getTitle());
        assertNotNull(firstnEWS.getNews());
        logger.info(firstnEWS.toJson().encodePrettily());
    }

    @Test
    public void shouldReceiveOneItemFromNewsStream() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Subscription subscription = new NewsTickerStream().getNewsObservable()
                .subscribe(news -> latch.countDown());
        latch.await();
        subscription.unsubscribe();
    }
}