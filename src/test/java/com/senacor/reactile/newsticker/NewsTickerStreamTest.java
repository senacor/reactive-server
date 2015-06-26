package com.senacor.reactile.newsticker;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
    public void testNewsStreamRunning() throws Exception {
        LinkedBlockingQueue<Boolean> monitor = new LinkedBlockingQueue<>();
        new NewsTickerStream().getNewsObservable()
                .subscribe(next -> System.out.println("next: " + next),
                        error -> error.printStackTrace(),
                        () -> {
                            System.out.println("completed");
                            try {
                                monitor.put(true);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
        monitor.poll(6, TimeUnit.SECONDS);
    }
}