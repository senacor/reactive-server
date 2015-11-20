package com.senacor.reactile.service.newsticker;

import com.google.common.base.MoreObjects;
import com.senacor.reactile.event.Event;
import com.senacor.reactile.json.Jsonizable;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class NewsChangedEvt implements Event<String>, Jsonizable {
    private final String id;
    private final NewsCollection newsCollection;

    public NewsChangedEvt() {
        this(null, null);
    }

    public NewsChangedEvt(NewsChangedEvt event) {
        this(event.id, event.newsCollection);
    }

    public NewsChangedEvt(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public NewsChangedEvt(
            String id,
            NewsCollection newsCollection) {
        this.id = id;
        this.newsCollection = newsCollection;
    }

    private NewsChangedEvt(Builder builder) {
        this(builder.id, builder.newsCollection);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public JsonObject toJson() {
        return new JsonObject()
                .put("id", null == id ? null : id)
                .put("newsCollection", null == newsCollection ? null : newsCollection.toJson());
    }

    public static NewsChangedEvt fromJson(JsonObject json) {
        return newBuilder()
                .withId(json.getString("id"))
                .withNewsCollection(NewsCollection.fromJson(json.getJsonObject("news")))
                .build();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("newsCollection", newsCollection)
                .toString();
    }


    public static final class Builder {
        private String id;
        private NewsCollection newsCollection;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withNewsCollection(NewsCollection newsCollection) {
            this.newsCollection = newsCollection;
            return this;
        }

        public NewsChangedEvt build() {
            return new NewsChangedEvt(this);
        }
    }
}
