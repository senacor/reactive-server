package com.senacor.reactile.domain;

import com.senacor.reactile.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class HttpResponseMatchers {

    public static Matcher<HttpResponse> hasHeader(String key, String value) {
        FeatureMatcher<HttpResponse, String> valueMatcher = new FeatureMatcher<HttpResponse, String>(equalTo(value), " a response header with key " + key + " and value", "value") {
            @Override
            protected String featureValueOf(HttpResponse actual) {
                return actual.headers().get(key);
            }
        };
        return allOf(hasHeader(key), valueMatcher);
    }

    public static Matcher<HttpResponse> hasHeader(String key) {
        return new TypeSafeDiagnosingMatcher<HttpResponse>() {
            @Override
            protected boolean matchesSafely(HttpResponse item, Description mismatchDescription) {
                boolean matches = item.headers().contains(key);
                if (!matches) {
                    mismatchDescription.appendText(
                            "header " + "'" + key + "'" + " was not present. The following headers are present: " + item.headers().names());
                }
                return matches;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a HttpResponse with a header " + "'" + key + "'");

            }
        };
    }

    public static Matcher<HttpResponse> hasStatus(int status) {
        return new FeatureMatcher<HttpResponse, Integer>(equalTo(status), " a response with status " + HttpResponseStatus.valueOf(status).reasonPhrase(), "status") {
            @Override
            protected Integer featureValueOf(HttpResponse actual) {
                return actual.statusCode();
            }
        };
    }

    public static Matcher<HttpResponse> hasStatus(HttpResponseStatus status) {
        return hasStatus(status.code());
    }

    public static Matcher<HttpResponse> has2xxStatus() {
        return inFamiliy(200);
    }

    public static Matcher<HttpResponse> has3xxStatus() {
        return inFamiliy(200);
    }

    public static Matcher<HttpResponse> has4xxStatus() {
        return inFamiliy(200);
    }

    private static Matcher<HttpResponse> inFamiliy(int family) {
        Matcher<Integer> expected = is(both(greaterThanOrEqualTo(family)).and(lessThanOrEqualTo(family + 99)));
        return new FeatureMatcher<HttpResponse, Integer>(expected, " status in 2xx family", "status") {
            @Override
            protected Integer featureValueOf(HttpResponse actual) {
                return actual.statusCode();
            }
        };
    }
}
