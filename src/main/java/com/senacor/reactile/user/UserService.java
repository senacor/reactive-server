package com.senacor.reactile.user;

import rx.Observable;

public interface UserService {

    Observable<User> getUser(UserId userId);
}
