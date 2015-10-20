package com.senacor.reactile.service.newsticker;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class NewsServiceImpl implements NewsService {

    @Override
    public void getLatestNews(int max, Handler<AsyncResult<NewsCollection>> resultHandler) {
    }

}
