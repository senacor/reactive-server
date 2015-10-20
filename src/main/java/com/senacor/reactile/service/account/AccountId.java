package com.senacor.reactile.service.account;

import com.senacor.reactile.IdObject;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

@DataObject
public class AccountId implements IdObject {

    private final String id;

    public AccountId() {
        this((String)null);
    }

    public AccountId(String id) {
        this.id = checkNotNull(id);
    }

    public AccountId(AccountId accountId) {
        this(accountId.getId());
    }

    public AccountId(JsonObject jsonObject) {
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
        final AccountId other = (AccountId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "AccountId{" +
                "id='" + id + '\'' +
                '}';
    }
}
