package com.senacor.reactile.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.senacor.reactile.AppModuleProvider;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.junit.rules.ExternalResource;

public class GuiceRule extends ExternalResource {

    private final io.vertx.rxjava.core.Vertx vertx;
    private final Object testInstance;

    public GuiceRule(io.vertx.rxjava.core.Vertx vertx, Object testInstance) {
        this.vertx = vertx;
        this.testInstance = testInstance;
    }

    @Override
    protected void before() throws Throwable {
        Injector injector = Guice.createInjector(new AppModuleProvider.AppModule(), new VertxModule((Vertx)vertx.getDelegate()));
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
