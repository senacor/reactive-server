package com.senacor.reactile.hystrix.interception;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import javax.inject.Inject;

import static org.apache.commons.lang3.Validate.notNull;
import static org.junit.Assert.assertEquals;

public class HystrixCommandInterceptorTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule();

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this, new TestModule());

    @Inject
    private TestService testService;

    @Inject
    TestCommandFactory testCommandFactory;

    @Test
    public void testCommand() throws Exception {
        String res = testService.doItCommand("lala").toBlocking().first();
        assertEquals("doneCommand: lala", res);
    }

    @Test
    public void testCommandFallback() throws Exception {
        String res = testService.doItCommandFallback("lala").toBlocking().first();
        assertEquals("fallback: lala", res);
    }

    @Test
    public void testCommandDefaultConstructor() throws Exception {
        String res = testService.doItCommandDefaultConstructor("lala").toBlocking().first();
        assertEquals("doneCommand: lala", res);
    }

    @Test
    public void testCommandDefaultConstructorFallback() throws Exception {
        String res = testService.doItCommandDefaultConstructorFallBack("lala").toBlocking().first();
        assertEquals("fallback: default", res);
    }

    @Test
    public void testCommandFactory() throws Exception {
        String res = testService.doItCommandFactory("lala").toBlocking().first();
        assertEquals("doneCommandFactory: lala", res);
    }

    @Test
    public void testCommandFactoryFallback() throws Exception {
        String res = testService.doItCommandFactoryFallback("lala").toBlocking().first();
        assertEquals("fallback: lala", res);
    }

    @Test
    public void testTestCommand() throws Exception {
        TestCommand lalaCommand = testCommandFactory.create("fallback");
        notNull(lalaCommand);
        String res = lalaCommand.withObservable(Observable.just("OK"))
                .toObservable().toBlocking().first();
        assertEquals("result:", "OK", res);
    }

    @Test
    public void testTestCommandFallback() throws Exception {
        TestCommand lalaCommand = testCommandFactory.create("lala");
        notNull(lalaCommand);
        String res = lalaCommand.withObservable(getErrorObservable())
                .toObservable().toBlocking().first();
        assertEquals("falback result:", "fallback: lala", res);
    }

    /**
     * Test Module to handle Test-specific guice stuff
     */
    private class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Install  HystrixComand Factories
            install(new FactoryModuleBuilder()
                    .implement(TestCommand.class, TestCommand.class)
                    .build(TestCommandFactory.class));
        }
    }

    /**
     * Test Service which has serveral methods intercepted with HystrixComands
     */
    public static class TestService {

        @HystrixCmd
        Observable<String> doIt(String s) {
            return Observable.just("done: " + s);
        }

        @HystrixCmd(TestCommand.class)
        Observable<String> doItCommand(String s) {
            return Observable.just("doneCommand: " + s);
        }

        @HystrixCmd(TestCommand.class)
        Observable<String> doItCommandFallback(String s) {
            return getErrorObservable();
        }

        @HystrixCmd(TestCommandDefaultConstructor.class)
        Observable<String> doItCommandDefaultConstructor(String s) {
            return Observable.just("doneCommand: " + s);
        }

        @HystrixCmd(TestCommandDefaultConstructor.class)
        Observable<String> doItCommandDefaultConstructorFallBack(String s) {
            return getErrorObservable();
        }

        @HystrixCmd(TestCommandFactory.class)
        Observable<String> doItCommandFactory(String s) {
            return Observable.just("doneCommandFactory: " + s);
        }

        @HystrixCmd(TestCommandFactory.class)
        Observable<String> doItCommandFactoryFallback(String s) {
            return getErrorObservable();
        }
    }

    private static Observable<String> getErrorObservable() {
        return Observable.error(new IllegalStateException("trigger fallback"));
    }

    /**
     * Test Command
     */
    public static class TestCommand extends InterceptableHystrixObservableCommand<String> {

        private final String s;

        @Inject
        public TestCommand(@Assisted String s) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("TestCommand"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));
            this.s = s;
        }

        @Override
        protected Observable<String> resumeWithFallback() {
            return Observable.just("fallback: " + s);
        }
    }

    /**
     * Test Command which has only a default constructor
     */
    public static class TestCommandDefaultConstructor extends TestCommand {
        public TestCommandDefaultConstructor() {
            super("default");
        }
    }

    /**
     * TestCommand Factory
     */
    public interface TestCommandFactory {
        TestCommand create(String s);
    }
}