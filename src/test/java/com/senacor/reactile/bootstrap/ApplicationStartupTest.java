package com.senacor.reactile.bootstrap;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

@Ignore
public class ApplicationStartupTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(Services.EmbeddedMongo).deployVerticle(ApplicationStartup.class);

    @Test(timeout = 8000)
    public void thatAllNecessaryVerticlesLaunched() throws InterruptedException {

        while(deploymentIDs().size() != 13){
            Thread.sleep(30);
        }
        Set<String> deployments = deploymentIDs();
        deployments.forEach(deployment -> System.out.println("deployment = " + deployment));

    }

    private Set<String> deploymentIDs() {
        return vertxRule.vertx().deploymentIDs();
    }
}
