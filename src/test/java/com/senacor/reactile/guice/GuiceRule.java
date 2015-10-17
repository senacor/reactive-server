package com.senacor.reactile.guice;

import com.google.common.collect.Lists;
import com.google.inject.*;
import com.senacor.reactile.AppModuleProvider;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.junit.rules.ExternalResource;

import java.util.List;

public class GuiceRule extends ExternalResource {

    private final io.vertx.rxjava.core.Vertx vertx;
    private final Object testInstance;
    private final Module[] additionalModules;

    public GuiceRule(io.vertx.rxjava.core.Vertx vertx, Object testInstance, Module... additionalModules) {
        this.vertx = vertx;
        this.testInstance = testInstance;
        this.additionalModules = additionalModules;
    }

    @Override
    protected void before() throws Throwable {
        List<Module> modules = Lists.asList(new AppModuleProvider.AppModule(),
                new VertxModule((Vertx) vertx.getDelegate()), additionalModules);
        Injector injector = Guice.createInjector(modules);
        injector.injectMembers(testInstance);
    }

    private class VertxModule extends AbstractModule {

        private final Vertx vertx;
        private final io.vertx.rxjava.core.Vertx rxVertx;

        VertxModule(Vertx vertx) {
            this.vertx = vertx;
            this.rxVertx = new io.vertx.rxjava.core.Vertx(vertx);
        }

        @Override
        protected void configure() {
            bind(Vertx.class).toInstance(vertx);
            bind(io.vertx.rxjava.core.Vertx.class).toInstance(rxVertx);
        }

        @Provides
        EventBus provideEventBus(Vertx vertx) {
            return vertx.eventBus();
        }

        @Provides
        io.vertx.rxjava.core.eventbus.EventBus provideRxEventBus(io.vertx.rxjava.core.Vertx vertx) {
            return vertx.eventBus();
        }
    }
}
