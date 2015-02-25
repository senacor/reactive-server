package com.senacor.reactile.auth;

import rx.Observable;

public interface UserService {

    Observable<User> getUser(UserId userId);
}
