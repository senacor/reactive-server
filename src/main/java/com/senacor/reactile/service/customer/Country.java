package com.senacor.reactile.service.customer;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Country implements Jsonizable {

    private final String name;
    private final String code;

    public Country() {
        this(null, null);
    }

    public Country(Country country) {
        this(country.name, country.code);
    }

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public JsonObject toJson() {
        return new JsonObject().put("name", name).put("code", code);
    }

    public static Country fromJson(JsonObject jsonObject) {
        return new Country(jsonObject.getString("name"), jsonObject.getString("code"));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
