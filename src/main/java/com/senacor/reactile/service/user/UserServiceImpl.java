package com.senacor.reactile.service.user;

import com.senacor.reactile.json.JsonizableList;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;

public class UserServiceImpl implements UserService {

    public static final String COLLECTION = "users";
    private final MongoService mongoService;

    @Inject
    public UserServiceImpl(MongoService mongoService, Vertx vertx) {
        this.mongoService = mongoService;
    }

    @Override
    public Observable<User> getUser(UserId userId){
        return mongoService.findOneObservable(COLLECTION, userId.toJson(), null)
                .map(User::fromJson);
    }

    @Override
    public Observable<JsonizableList<JsonObject>> findUser(JsonObject query){
        return mongoService.findObservable(COLLECTION, query)
                .map(list -> new JsonizableList<JsonObject>(list));
    }


    @Override

    public Observable<User> login(UserId userId)
    {
        return getUser(userId);
    }

    @Override
    public Observable<User> createUser(User user) {
        JsonObject userJson = user.toJson().put("_id", user.getId().toValue());
        return mongoService
                .insertObservable(COLLECTION, userJson)
                .flatMap(res -> Observable.just(user));

    }

}
