package com.senacor.reactile.service.branch;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.senacor.reactile.domain.IdObject;
import com.senacor.reactile.json.Jsonizable;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class BranchId implements IdObject, Jsonizable {

    private final String id;

    public BranchId() {
        this((String)null);
    }

    public BranchId(String id) {
        this.id = checkNotNull(id);
    }

    public BranchId(BranchId branchId) {
        this(branchId.getId());
    }

    public BranchId(JsonObject jsonObject) {
        this(jsonObject.getString("id"));
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public JsonObject toJson() {
        return new JsonObject().put("id", toValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final BranchId other = (BranchId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "BranchId{" +
                "id='" + id + '\'' +
                '}';
    }
}
