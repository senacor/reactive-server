package com.senacor.reactile.service.newsticker;

import com.google.common.base.MoreObjects;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.json.Jsonizable;

import io.vertx.core.json.JsonObject;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class NewsItemsUpdatedEvent implements Event<String>, Jsonizable {
    private final String title;
    private final News newsItem;

    public NewsItemsUpdatedEvent() {
        this(null, null);
    }

    public NewsItemsUpdatedEvent(NewsItemsUpdatedEvent event) {
        this(event.title, event.newsItem);
    }

    public NewsItemsUpdatedEvent(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public NewsItemsUpdatedEvent(String title, News newsItem) {
        this.title = title;
        this.newsItem = newsItem;
    }

    private NewsItemsUpdatedEvent(Builder builder) {
        this(builder.id, builder.newsItem);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public JsonObject toJson() {
        return new JsonObject() //
            .put("title", title) //
            .put("news", newsItem == null ? null : newsItem.getNews());
    }

    public static NewsItemsUpdatedEvent fromJson(JsonObject json) {
        return newBuilder().withId(json.getString("title")).build();
    }

    @Override
    public String getId() {
        return title;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("title", title).toString();
    }

    public static final class Builder {
        private String id;
        private News newsItem;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withNews(News newsItem) {
            this.newsItem = newsItem;
            return this;
        }

        public NewsItemsUpdatedEvent build() {
            return new NewsItemsUpdatedEvent(this);
        }
    }
}
