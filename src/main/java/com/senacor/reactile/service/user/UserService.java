package com.senacor.reactile.service.user;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

@ProxyGen
@VertxGen
public interface UserService {


    String ADDRESS = "UserService";

    void getUser(UserId userId, Handler<AsyncResult<User>> resultHandler);

    void findUser(JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    void login(UserId userId, Handler<AsyncResult<User>> resultHandler);

    void createUser(User user, Handler<AsyncResult<User>> resultHandler);

}
