package com.senacor.reactile.service.user;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    public static final String COLLECTION = "users";
    private final MongoService mongoService;

    @Inject
    public UserServiceImpl(MongoService mongoService, Vertx vertx) {
        this.mongoService = mongoService;
    }

    @Override
    public void getUser(UserId userId, Handler<AsyncResult<User>> resultHandler) {
        Rx.bridgeHandler(getUser(userId), resultHandler);
    }

    @Override
    public void findUser(JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        Rx.bridgeHandler(mongoService.findObservable(COLLECTION, query), resultHandler);
    }


    @Override
    public void login(UserId userId, Handler<AsyncResult<User>> resultHandler) {
        getUser(userId, resultHandler);
    }

    @Override
    public void createUser(User user, Handler<AsyncResult<User>> resultHandler) {
        JsonObject userJson = user.toJson().put("_id", user.getId().toValue());
        Rx.bridgeHandler(
                mongoService.insertObservable(COLLECTION, userJson)
                        .flatMap(res -> Observable.just(user))
                , resultHandler);

    }

    private Observable<User> getUser(UserId userId) {
        return mongoService.findOneObservable(COLLECTION, userId.toJson(), null)
                .map(User::fromJson);
    }
}
