package com.senacor.reactile.bootstrap;

import com.senacor.reactile.VertxRule;
import io.vertx.rxjava.core.Vertx;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartupTest {
    @Rule
    public final VertxRule vertxRule = new VertxRule(ApplicationStartup.class);
    private final Vertx vertx = vertxRule.vertx();

    @Test(timeout = 5000)
    public void thatAllNecessaryVerticlesLaunched() throws InterruptedException {
        Set<String> deployments = vertx.deploymentIDs();
        deployments.forEach(deployment -> System.out.println("deployment = " + deployment));

        while(deployments.size() == 3){
            Thread.sleep(100);
        }

    }
}
