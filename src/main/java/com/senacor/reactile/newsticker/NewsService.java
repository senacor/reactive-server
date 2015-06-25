package com.senacor.reactile.newsticker;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface NewsService {

    static final String ADDRESS = "NewsService";

    void getNews(Handler<AsyncResult<News>> resultHandler);

}
