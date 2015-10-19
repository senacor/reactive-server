package com.senacor.reactile.service.user;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface UserService {


    String ADDRESS = "UserService";

    void getUser(UserId userId, Handler<AsyncResult<User>> resultHandler);

    void login(UserId userId, Handler<AsyncResult<User>> resultHandler);
}
