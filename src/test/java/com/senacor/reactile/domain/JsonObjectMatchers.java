package com.senacor.reactile.domain;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

public class JsonObjectMatchers {


    public static Matcher<JsonObject> hasValue(String property, Object value) {
        FeatureMatcher<JsonObject, Object> valueMatcher = new FeatureMatcher<JsonObject, Object>(equalTo(value), "a json object with property " + property + " and value", "value") {
            @Override
            protected Object featureValueOf(JsonObject actual) {
                return actual.getValue(property);
            }
        };
        return allOf(hasProperty(property), valueMatcher);
    }

    public static Matcher<JsonArray> hasSize(int expectedSize) {
        return new FeatureMatcher<JsonArray, Integer>(equalTo(expectedSize), "a json array with size", "size") {
            @Override
            protected Integer featureValueOf(JsonArray actual) {
                return actual.size();
            }
        };
    }

    public static Matcher<JsonArray> empty() {
        return hasSize(0);
    }

    public static Matcher<JsonObject> hasProperty(String property) {
        return new TypeSafeDiagnosingMatcher<JsonObject>() {
            @Override
            protected boolean matchesSafely(JsonObject item, Description mismatchDescription) {
                boolean matches = item.containsKey(property);
                if (!matches) {
                    mismatchDescription.appendText(
                            "property " + "'" + property + "'" + " was not present. The following keys are present: " + quoteEntries(item.fieldNames()));
                }
                return matches;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a JsonObject with a property " + "'" + property + "'");
            }
        };
    }

    public static Matcher<JsonObject> hasProperties(String aProperty, String anotherProperty, String... moreProprties) {
        return new TypeSafeDiagnosingMatcher<JsonObject>() {
            @Override
            protected boolean matchesSafely(JsonObject item, Description mismatchDescription) {
                Set<String> required = required();
                HashSet<String> missing = newHashSet(required);
                missing.removeAll(item.fieldNames());
                boolean matches = missing.isEmpty();
                if (!matches) {
                    mismatchDescription.appendText(
                            "Expected properties " + missing + " were not present. The following properties are present: "
                                    + quoteEntries(item.fieldNames()));
                }
                return matches;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a JsonObject with properties " + required());
            }

            private Set<String> required() {
                Set<String> required = newHashSet(moreProprties);
                required.add(anotherProperty);
                required.add(aProperty);
                return required;
            }
        };
    }

    private static String quoteEntries(Collection<? extends Object> entries) {
        return "[" +
                entries.stream()
                        .map(property -> "\"" + property.toString() + "\"")
                        .collect(Collectors.joining(", ")) +
                "]";
    }


}
