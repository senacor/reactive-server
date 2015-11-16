package com.senacor.reactile.domain;

import com.senacor.reactile.json.Jsonizable;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static org.hamcrest.Matchers.equalTo;

public class JsonizableMatchers {


    public static Matcher<Jsonizable> hasValue(String property, Object value) {
        return new FeatureMatcher<Jsonizable, Object>(equalTo(value), " an object with property " + property + " and value", "value") {
            @Override
            protected Object featureValueOf(Jsonizable actual) {
                return actual.toJson().getValue(property);
            }
        };
    }

    public static Matcher<Jsonizable> hasProperty(String property) {
        return new TypeSafeDiagnosingMatcher<Jsonizable>() {
            @Override
            protected boolean matchesSafely(Jsonizable item, Description mismatchDescription) {
                return item.toJson().containsKey(property);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
