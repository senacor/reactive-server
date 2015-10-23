package com.senacor.reactile.service.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senacor.reactile.domain.Identity;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject
public class User implements Identity<UserId>, Jsonizable {

    private final UserId id;
    private final String firstName;
    private final String lastName;
    private final String branchId;

    public User() {
        this(null, null, null, null);
    }

    public User(
            @JsonProperty("id") UserId id,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("branchId") String branchId
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.branchId = branchId;
    }

    public User(User user) {
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getBranchId());
    }

    public User(JsonObject jsonObject) {
        this(fromJson(jsonObject));
    }

    public static Builder aUser() {
        return new Builder();
    }

    public UserId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, branchId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.firstName, other.firstName)
                && Objects.equals(this.lastName, other.lastName)
                && Objects.equals(this.branchId, other.branchId);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", branchId='" + branchId + '\'' +
                '}';
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.toValue())
                .put("firstName", firstName)
                .put("lastName", lastName)
                .put("branchId", branchId);
    }

    public static User fromJson(JsonObject jsonObject) {
        return aUser()
                .withId(jsonObject.getString("id"))
                .withFirstName(jsonObject.getString("firstName"))
                .withLastName(jsonObject.getString("lastName"))
                .withBranch(jsonObject.getString("branchId"))
                .build();
    }

    public String getBranchId() {
        return branchId;
    }

    public static final class Builder {
        private UserId id;
        private String firstName;
        private String lastName;
        private String branchId;

        private Builder() {
        }

        public Builder withId(UserId id) {
            this.id = id;
            return this;
        }

        public Builder withId(String id) {
            this.id = new UserId(id);
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withBranch(String branch) {
            this.branchId = branch;
            return this;
        }

        public User build() {
            return new User(id, firstName, lastName, branchId);
        }
    }
}
