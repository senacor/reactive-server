package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.abstractservice.Action;
import rx.Observable;

public interface NewsService {
    String ADDRESS = "NewsService";

    @Action(returnType = NewsCollection.class)
    public Observable<NewsCollection> getLatestNews(Integer max);

}
