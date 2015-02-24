package com.senacor.reactile.bootstrap;

import com.senacor.reactile.VertxRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartupTest {
    @Rule
    public final VertxRule vertxRule = new VertxRule(ApplicationStartup.class);

    @Test
    public void thatAllNecessaryVerticlesLaunched() {
        // Set<String> expectedDeployments = new HashSet<>();
        System.err.println("checking for deployments");
        Set<String> deployments = vertxRule.vertx().deployments();
        for (String deployment : deployments) {
            System.out.println("deployment = " + deployment);
        }
    }
}
