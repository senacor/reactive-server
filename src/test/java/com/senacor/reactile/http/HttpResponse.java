package com.senacor.reactile.http;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpClientResponse;

import java.util.stream.Collectors;

/**
 * Response for Body at once Requests (Default)
 */
public interface HttpResponse {

    HttpClientResponse getHttpClientResponse();

    int statusCode();

    String statusMessage();

    MultiMap headers();

    default String headersAsString() {
        return headers().names().stream()
                .map(key -> key + "=" + headers().get(key))
                .collect(Collectors.toList()).toString();
    }

    String getBody();

    default JsonObject asJson() {
        return new JsonObject(getBody());
    }
}
