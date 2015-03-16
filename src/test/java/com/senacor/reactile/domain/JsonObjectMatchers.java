package com.senacor.reactile.domain;

import io.vertx.core.json.JsonObject;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static org.hamcrest.Matchers.equalTo;

public class JsonObjectMatchers {


    public static Matcher<JsonObject> hasValue(String property, Object value) {
        return new FeatureMatcher<JsonObject, Object>(equalTo(value), " a json object with property " + property + " and value", "value") {
            @Override
            protected Object featureValueOf(JsonObject actual) {
                return actual.getValue(property);
            }
        };
    }

    public static Matcher<JsonObject> hasProperty(String property) {
        return new TypeSafeDiagnosingMatcher<JsonObject>() {
            @Override
            protected boolean matchesSafely(JsonObject item, Description mismatchDescription) {
                return item.containsKey(property);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
