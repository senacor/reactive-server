package com.senacor.reactile.service.newsticker;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface NewsService {
    String ADDRESS = "NewsService";

    /**
     * Get the latest news.
     *
     * @param max Maximum number of latest news items per page. Must be between 1 and 1000.
     * @param resultHandler The handler function to consume a news page.
     */
    void getLatestNews(int max, Handler<AsyncResult<NewsCollection>> resultHandler);

}
