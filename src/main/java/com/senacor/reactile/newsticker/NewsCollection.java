package com.senacor.reactile.newsticker;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshal;

@DataObject
public class NewsCollection implements Jsonizable{

    private final List<News> news;

    public NewsCollection() {
        news = new ArrayList<>();
    }

    public NewsCollection(List<News> news) {
        this.news = news;
    }

    public NewsCollection(NewsCollection old) {
        news = new ArrayList<>(old.getNews());
    }

    public NewsCollection(JsonObject jsonObject) {
         news = fromJson(jsonObject).getNews();
    }

    public List<News> getNews() {
        return news;
    }

    public static NewsCollection fromJson(JsonObject jsonObject) {
        checkArgument(jsonObject != null);
        return new NewsCollection(unmarshal(jsonObject.getJsonArray("news"), News::fromJson));
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("news", marshal(news, News::toJson));
    }

}
