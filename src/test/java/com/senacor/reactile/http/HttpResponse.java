package com.senacor.reactile.http;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpClientResponse;

public interface HttpResponse {

    HttpClientResponse getHttpClientResponse();

    int statusCode();

    String statusMessage();

    MultiMap headers();

    String getBody();

    JsonObject asJson();


}
