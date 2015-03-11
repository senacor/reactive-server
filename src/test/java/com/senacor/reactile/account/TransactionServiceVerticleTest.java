package com.senacor.reactile.account;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.ApplicationStartup;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by rwinzing on 03.03.15.
 */
public class TransactionServiceVerticleTest {
    @Rule
    public final VertxRule vertxRule = new VertxRule(ApplicationStartup.class);
    {
        vertxRule.deployVerticle(Services.TransactionService);
    }

    @Test
    public void thatVerticleLaunches() throws InterruptedException, ExecutionException, TimeoutException {
        Thread.sleep(1000);
    }
}
