package com.senacor.reactile.gateway.commands;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.senacor.reactile.json.JsonObjects;
import com.senacor.reactile.rxjava.service.newsticker.NewsService;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import javax.inject.Inject;

/**
 * Command to collect the start-page data
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 16.04.15
 * Time: 15:25
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class NewsCommand extends HystrixObservableCommand<JsonObject> {


    private final NewsService newsService;
    private final int number;

    @Inject
    public NewsCommand(NewsService newsService,
                       @Assisted Integer number) {


        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Gateway"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("News"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.newsService = newsService;
        this.number = number.intValue();
    }

    @Override
    protected Observable<JsonObject> construct() {
        return newsService.getLatestNewsObservable(number).map(collection -> collection.getNews())
                .map(JsonObjects::toJsonArray)
                .map(jsonArray -> new JsonObject().put("news", jsonArray));
    }

}
