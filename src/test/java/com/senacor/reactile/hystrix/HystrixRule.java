package com.senacor.reactile.hystrix;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.junit.rules.ExternalResource;

public class HystrixRule extends ExternalResource {

    @Override
    protected void before() {
        HystrixPlugins.reset();
        Hystrix.reset();
    }
}
