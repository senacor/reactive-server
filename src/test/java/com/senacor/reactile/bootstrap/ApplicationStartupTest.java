package com.senacor.reactile.bootstrap;

import com.senacor.reactile.VertxRule;
import io.vertx.rxjava.core.Vertx;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartupTest {
    @Rule
    public final VertxRule vertxRule = new VertxRule(ApplicationStartup.class);
    private final Vertx vertx = vertxRule.vertx();

    @Test
    public void thatAllNecessaryVerticlesLaunched() throws InterruptedException {
        Thread.sleep(5000);

        Set<String> deployments = vertx.deploymentIDs();
        for (String deployment: deployments) {
            System.out.println("deployment = " + deployment);
        }

        assertThat(deployments.size(), is(equalTo(3)));
    }
}
