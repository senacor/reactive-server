package com.senacor.reactile.service.branch;

import com.senacor.reactile.domain.Jsonizable;
import com.senacor.reactile.service.customer.Address;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Andreas Keefer
 */
@DataObject
public class Branch implements Jsonizable {

    private final String id;
    private final String name;
    private final Address address;

    public Branch() {
        this(null, null, null);
    }

    public Branch(String id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public Branch(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public Branch(Branch branch) {
        this(Branch.newBuilder(branch));
    }

    private Branch(Builder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
    }

    public static Builder newBuilder(Branch copy) {
        return new Builder(copy.id).withAddress(copy.getAddress()).withName(copy.getName());
    }

    public static Builder newBuilder(String id) {
        return new Builder(id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * @param newAddress new Address
     * @return new Branch with replaced or added Address
     */
    public static Branch addOrReplaceAddress(Branch branch, Address newAddress) {
        return Branch.newBuilder(branch).withAddress(newAddress).build();
    }

    public static Branch fromJson(JsonObject jsonObject) {
        if (null == jsonObject) {
            throw new IllegalArgumentException("banane");
        }
        return Branch.newBuilder("id")
                .withId(jsonObject.getString("id"))
                .withName(jsonObject.getString("name"))
                .withAddress(Address.fromJson(jsonObject.getJsonObject("address")))
                .build();
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("name", name)
                .put("address", null == address ? null : address.toJson());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


    public static final class Builder {
        private String id;
        private String name;
        private Address address;

        private Builder(String id) {
            withId(id);
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddress(Address address) {
            this.address = address;
            return this;
        }

        public Branch build() {
            return new Branch(this);
        }
    }
}
