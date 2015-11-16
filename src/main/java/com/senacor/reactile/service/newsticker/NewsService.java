package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.service.branch.Branch;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

public interface NewsService {
    String ADDRESS = "NewsService";

    @Action(returnType = NewsCollection.class)
    public Observable<NewsCollection> getLatestNews(int max);

}
