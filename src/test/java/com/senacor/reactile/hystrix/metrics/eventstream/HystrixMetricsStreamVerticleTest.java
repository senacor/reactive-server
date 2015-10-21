package com.senacor.reactile.hystrix.metrics.eventstream;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.http.HttpResponseStream;
import com.senacor.reactile.http.HttpTestClient;
import com.senacor.reactile.hystrix.HystrixRule;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.http.HttpClient;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import static com.senacor.reactile.domain.HttpResponseMatchers.hasHeader;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HystrixMetricsStreamVerticleTest {

    private static final Logger logger = LoggerFactory.getLogger(HystrixMetricsStreamVerticleTest.class);

    @Rule
    public final HystrixRule hystrixRule = new HystrixRule();

    @Rule
    public final VertxRule vertxRule = new VertxRule(Services.HystrixMetricsStreamVerticle);

    private final HttpClient httpClientHystrixStream = vertxRule.vertx().createHttpClient(
            new HttpClientOptions().setDefaultPort(8082).setDefaultHost("localhost"));
    private final HttpTestClient testHttpClientHystrixStream = new HttpTestClient(httpClientHystrixStream);

    @Test
    public void thatRequestsAreHandled() throws Exception {
        // 1. create an execute a HystricCommand to get some data
        String testCommandRes = new HystrixObservableCommand<String>(HystrixObservableCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("DummyTest"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50))) {
            @Override
            protected Observable<String> construct() {
                return Observable.just("Test");
            }
        }.toObservable().toBlocking().first();
        assertEquals("Test", testCommandRes);

        // 2. get data from hystrix.stream endpoint
        HttpResponseStream response = testHttpClientHystrixStream.getAsStream("/hystrix.stream?delay=500");
        logger.info("header: " + response.headersAsString());
        assertThat(response, hasHeader("Content-Type", "text/event-stream;charset=UTF-8"));
        assertThat(response, hasHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate"));
        assertThat(response, hasHeader("Pragma", "no-cache"));
        assertThat(response, hasHeader("Transfer-Encoding", "chunked"));
        String responseStream = response.getNextData();
        assertThat(responseStream, startsWith("data: {"));
        assertThat(responseStream, containsString("\"group\":\"DummyTest\""));
    }
}