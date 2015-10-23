package com.senacor.reactile.gateway.commands;

import com.senacor.reactile.service.user.UserId;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.core.json.JsonObject;

/**
 * Created by swalter on 23.10.15.
 */
public interface UserFindCommandFactory {

    UserFindCommand create();

}
