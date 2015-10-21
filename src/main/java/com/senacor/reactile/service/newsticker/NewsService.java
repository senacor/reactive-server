package com.senacor.reactile.service.newsticker;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface NewsService {
    String ADDRESS = "NewsService";

    void getLatestNews(int max, Handler<AsyncResult<NewsCollection>> resultHandler);

}
