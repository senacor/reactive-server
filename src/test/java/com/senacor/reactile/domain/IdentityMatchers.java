package com.senacor.reactile.domain;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;

public class IdentityMatchers {


    public static Matcher<Identity<?>> hasId(final String id) {
        return new FeatureMatcher<Identity<?>, String>(equalTo(id), "domain object with Id", "Id") {
            @Override
            protected String featureValueOf(Identity<?> actual) {
                return actual.getId().getId();
            }
        };
    }

    public static Matcher<Identity<?>> hasId(final IdObject id) {
        return new FeatureMatcher<Identity<?>, String>(equalTo(id.getId()), "domain object with Id", "Id") {
            @Override
            protected String featureValueOf(Identity<?> actual) {
                return actual.getId().getId();
            }
        };
    }

}
