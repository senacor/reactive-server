package com.senacor.reactile.service.creditcard;

import com.google.common.collect.ImmutableList;
import com.senacor.reactile.json.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshal;

@DataObject
public class CreditCardList implements Jsonizable {

    private final List<CreditCard> creditCardList;

    public CreditCardList() {
        this.creditCardList = Collections.emptyList();
    }

    public CreditCardList(List<CreditCard> creditCardList) {
        this.creditCardList = creditCardList == null ? Collections.emptyList() : ImmutableList.copyOf(creditCardList);
    }

    public CreditCardList(CreditCardList creditCardList) {
        this.creditCardList = ImmutableList.copyOf(creditCardList.getCreditCardList());
    }

    public CreditCardList(JsonObject jsonObject) {
        this.creditCardList = fromJson(jsonObject).getCreditCardList();
    }

    public List<CreditCard> getCreditCardList() {
        return creditCardList;
    }

    public static CreditCardList fromJson(JsonObject jsonObject) {
        checkArgument(jsonObject != null);
        return new CreditCardList(unmarshal(jsonObject.getJsonArray("creditCardList"), CreditCard::fromJson));
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("creditCardList", marshal(creditCardList, CreditCard::toJson));
    }
}
