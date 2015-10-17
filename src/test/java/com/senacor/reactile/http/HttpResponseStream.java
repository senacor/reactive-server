package com.senacor.reactile.http;

import io.vertx.core.json.JsonObject;

/**
 * Response for Streaming / Big Data
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 21.04.15
 * Time: 13:58
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public interface HttpResponseStream extends HttpResponse {
    String getNextData();

    default JsonObject getNextDataAsJson() {
        return new JsonObject(getNextData());
    }
}
