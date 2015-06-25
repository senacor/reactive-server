package com.senacor.reactile.newsticker;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface NewsService {

    static final String ADDRESS = "NewsService";

    static final String ADDRESS_EVENT_UPDATE_ADDRESS = NewsService.ADDRESS + "#getNews";

    void getNews(Handler<AsyncResult<News>> resultHandler);

}
