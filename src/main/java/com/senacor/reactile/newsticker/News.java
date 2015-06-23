package com.senacor.reactile.newsticker;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Andreas Keefer
 */
@DataObject
public class News implements Jsonizable {

    private final String title;
    private final String news;

    public News() {
        this(null, null);
    }

    public News(String title, String news) {
        this.title = title;
        this.news = news;
    }

    public News(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public News(News news) {
        this(news.getTitle(),
                news.getNews());
    }

    private News(Builder builder) {
        title = builder.title;
        news = builder.news;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public String getNews() {
        return news;
    }

    public static News fromJson(JsonObject jsonObject) {
        return null == jsonObject ? null : News.newBuilder()
                .withTitle(jsonObject.getString("title"))
                .withNews(jsonObject.getString("news"))
                .build();
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("title", title)
                .put("news", news);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


    public static final class Builder {
        private String title;
        private String news;

        private Builder() {
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withNews(String news) {
            this.news = news;
            return this;
        }

        public News build() {
            return new News(this);
        }
    }
}
