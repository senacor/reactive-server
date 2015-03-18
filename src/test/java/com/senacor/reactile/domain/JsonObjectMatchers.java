package com.senacor.reactile.domain;

import io.vertx.core.json.JsonObject;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

public class JsonObjectMatchers {


    public static Matcher<JsonObject> hasValue(String property, Object value) {
        FeatureMatcher<JsonObject, Object> valueMatcher = new FeatureMatcher<JsonObject, Object>(equalTo(value), " a json object with property " + property + " and value", "value") {
            @Override
            protected Object featureValueOf(JsonObject actual) {
                return actual.getValue(property);
            }
        };
        return allOf(hasProperty(property), valueMatcher);
    }

    public static Matcher<JsonObject> hasProperty(String property) {
        return new TypeSafeDiagnosingMatcher<JsonObject>() {
            @Override
            protected boolean matchesSafely(JsonObject item, Description mismatchDescription) {
                boolean matches = item.containsKey(property);
                if (!matches) {
                    mismatchDescription.appendText(
                            "property " + "'" + property + "'" + " was not present. The following keys are present: " + item.fieldNames());
                }
                return matches;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a JsonObject with a property " + "'" + property + "'");
            }
        };
    }

}
