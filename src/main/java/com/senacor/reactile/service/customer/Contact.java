package com.senacor.reactile.service.customer;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@DataObject
public class Contact implements Jsonizable {
    public Contact(Contact contact) {
    }

    public Contact(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public Contact() {
    }


    public JsonObject toJson() {
        return new JsonObject();
    }

    public static Contact fromJson(JsonObject jsonObject) {
        return new Contact();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
