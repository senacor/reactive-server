package com.senacor.reactile.service.newsticker;

import com.senacor.reactile.abstractservice.Action;
import rx.Observable;

public interface NewsService {
    String ADDRESS = "NewsService";

    public static final String NEWS_STREAM = "NEWS_STREAM";

    @Action(returnType = NewsCollection.class)
    public Observable<NewsCollection> getLatestNews(Integer max);

    @Action(returnType = News.class, pattern = Action.MessagePattern.PublishSubsrcribe)
    public Observable<News> streamNews();

}
