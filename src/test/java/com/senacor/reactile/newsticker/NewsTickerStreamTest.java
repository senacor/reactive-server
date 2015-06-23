package com.senacor.reactile.newsticker;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.Test;

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
}