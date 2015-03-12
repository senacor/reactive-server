package com.senacor.reactile.bootstrap;

import com.senacor.reactile.Services;
import com.senacor.reactile.TestServices;
import com.senacor.reactile.VertxRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

/**
 * Created by rwinzing on 24.02.15.
 */
public class ApplicationStartupTest {
    @Rule
    public final VertxRule vertxRule = new VertxRule(TestServices.EmbeddedMongo).deployVerticle(ApplicationStartup.class);

    @Test(timeout = 1000)
    public void thatAllNecessaryVerticlesLaunched() throws InterruptedException {

        while(deploymentIDs().size() != Services.values().length + 1){
            Thread.sleep(30);
        }
        Set<String> deployments = deploymentIDs();
        deployments.forEach(deployment -> System.out.println("deployment = " + deployment));

    }

    private Set<String> deploymentIDs() {
        return vertxRule.vertx().deploymentIDs();
    }
}
