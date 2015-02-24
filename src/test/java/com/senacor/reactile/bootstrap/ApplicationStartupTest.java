package com.senacor.reactile.bootstrap;

import com.senacor.reactile.gateway.VertxRule;
import io.vertx.rxjava.core.Vertx;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartupTest {
    private final Vertx vertx = Vertx.vertx();

    @Rule
    public final VertxRule vertxRule = new VertxRule(ApplicationStartup.class);

    @Test
    public void thatAllNecessaryVerticlesLaunched() {
        // Set<String> expectedDeployments = new HashSet<>();
        System.err.println("checking for deployments");
        Set<String> deployments = vertx.deployments();
        for (String dpeloyment: deployments) {
            System.out.println("deployment = " + dpeloyment);
        }
    }
}
