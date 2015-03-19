package com.senacor.reactile.customer;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

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
}
