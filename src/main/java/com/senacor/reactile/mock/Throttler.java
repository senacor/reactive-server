package com.senacor.reactile.mock;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.TreeSet;

/**
 * Service, der dazu verwendet werden kann, um einen ansteigenden Delay anhand der
 * Calls per Second zu simulieren (BLOCKING).
 *
 * @author Andreas Keefer
 */
public class Throttler {

    private static final Logger logger = LoggerFactory.getLogger(Throttler.class);

    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    private static final int capacity = 1000;
    private final TreeSet<Long> calls = new TreeSet<>();

    /**
     * Pausiert den Thread. Je hoeher die calls per second, je laenger der delay
     *
     * @param faktor damit kann der delay des services linear verlaengert oder verkuerzt werden
     * @return die Zeit, die der Thread geschlafen hat
     */
    public Long delayed(final BigDecimal faktor) {
        long start = System.currentTimeMillis();
        add(start);

        // calculate delay
        final BigDecimal callCount = BigDecimal.valueOf(calls.size());
        BigDecimal timespan = BigDecimal.valueOf(start - calls.first());
        if (0 == BigDecimal.ZERO.compareTo(timespan)) {
            timespan = BigDecimal.ONE;
        }

        // calculate calls per second
        final BigDecimal callsPerSecond = callCount.divide(timespan, 4, RoundingMode.HALF_UP)
                .multiply(THOUSAND)
                .setScale(1, RoundingMode.HALF_UP);


        BigDecimal delay;
        BigDecimal addition = BigDecimal.ONE;
        if (callCount.intValue() < 10) {
            delay = callsPerSecond.divide(BigDecimal.valueOf(5), 0, RoundingMode.HALF_UP);
        } else {
            if (BigDecimal.ONE.compareTo(callsPerSecond) < 0) {
                addition = BigDecimal.valueOf(Math.log(callsPerSecond.doubleValue())).abs();
            }
            delay = callsPerSecond.multiply(addition).setScale(0, RoundingMode.HALF_UP);
        }

        delay = delay.multiply(faktor);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("callCount=%s in %sms (=%s calls per Second) (addition=%s) -> delay=%s",
                    callCount, timespan, callsPerSecond, addition, delay));
        }
        try {
            Thread.sleep(delay.intValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() - start;
    }

    /**
     * Pausiert den Thread. Je hoeher die calls per second, je laenger der delay
     *
     * @return die Zeit, die der Thread geschlafen hat
     */
    public Long delayed() {
        return delayed(BigDecimal.ONE);
    }

    /**
     * Pausiert den Thread. Je hoeher die calls per second, je laenger der delay
     *
     * @return die Zeit, die der Thread geschlafen hat als Observable
     */
    public Observable<Long> delayedObservable() {
        return Observable.create((Subscriber<? super Long> s) -> {
            s.onNext(delayed());
            s.onCompleted();
        });
    }

    /**
     * Pausiert den Thread. Je hoeher die calls per second, je laenger der delay
     *
     * @return die Zeit, die der Thread geschlafen hat als Observable mit IO Scheduler
     */
    public Observable<Long> delayedObservableAsync() {
        return delayedObservable()
                .subscribeOn(Schedulers.io());
    }

    private synchronized void add(long timestamp) {
        if (calls.size() >= capacity) {
            calls.pollFirst();
        }
        calls.add(timestamp);
    }
}
