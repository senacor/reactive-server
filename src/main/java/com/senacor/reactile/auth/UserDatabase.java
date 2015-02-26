package com.senacor.reactile.auth;


public interface UserDatabase {

    User login(UserId userId);

    User findUser(UserId id);

    void addUser(User user);
}
