package com.senacor.reactile.appointment;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshal;

/**
 * @author Andreas Keefer
 */
@DataObject
public class BranchList implements Jsonizable {

    private final List<Branch> branches;

    public BranchList() {
        this(BranchList.newBuilder());
    }

    public BranchList(BranchList branchList) {
        this(BranchList.newBuilder(branchList));
    }

    public BranchList(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    private BranchList(Builder builder) {
        branches = builder.branches;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(BranchList copy) {
        Builder builder = new Builder();
        builder.branches = copy.branches;
        return builder;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public static BranchList fromJson(JsonObject jsonObject) {
        return null == jsonObject ? null : BranchList.newBuilder()
                .withBranches(unmarshal(jsonObject.getJsonArray("branches"), Branch::fromJson))
                .build();
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("branches", marshal(branches, Branch::toJson));
    }

    public static final class Builder {
        private List<Branch> branches;

        private Builder() {
        }

        public Builder withBranches(List<Branch> branches) {
            this.branches = branches;
            return this;
        }

        public BranchList build() {
            return new BranchList(this);
        }
    }
}
