package com.senacor.reactile.mongo;

import com.senacor.reactile.VertxRule;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.senacor.reactile.TestServices.EmbeddedMongo;

public class EmbeddedMongoVerticleTest {

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(EmbeddedMongo);

    @Test(timeout = 1000)
    public void thatMongoServiceCanBeDeployed() throws Exception {
        while(vertxRule.vertx().deploymentIDs().size() != 2) {
            TimeUnit.MILLISECONDS.sleep(30);
        }


    }
}