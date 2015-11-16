package com.senacor.reactile.service.user;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.abstractservice.JsonizableList;
import io.vertx.core.json.JsonObject;
import rx.Observable;

public interface UserService {

    @Action(returnType = User.class)
    public Observable<User> getUser(UserId userId);

    @Action(returnType = JsonizableList.class)
    public Observable<JsonizableList<JsonObject>> findUser(JsonObject query);

    @Action(returnType = User.class)
    public Observable<User> login(UserId userId);

    @Action(returnType = User.class)
    public Observable<User> createUser(User user);

}
