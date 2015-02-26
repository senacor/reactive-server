package com.senacor.reactile.auth;

import com.google.common.collect.ImmutableMap;

import java.util.AbstractMap;
import java.util.Map;

public class SimpleUserDatabase implements UserDatabase {

    private final Map<UserId, User> users;

    {
        users = ImmutableMap.<UserId, User>builder()
                .put(user("momann", "Michael", "Omann"))
                .put(user("rwinzinger", "Ralph", "Winzinger"))
                .put(user("mmenzel", "Michael", "Menzel"))
                .put(user("aloch", "Andreas", "Loch"))
                .put(user("adick", "Andreas", "Dick"))
                .put(user("cstar", "Cinnamon", "Star"))
                .put(user("aangel", "Aurora", "Angel"))
                .put(user("cross", "Crystal", "Ross"))
                .build();
    }

    private static Map.Entry<UserId, User> user(String shortName, String fistName, String lastName) {
        UserId id = new UserId(shortName);
        return new AbstractMap.SimpleEntry<>(id, new User(id, fistName, lastName));
    }


    @Override
    public User login(UserId id) {
        return users.get(id);
    }

    @Override
    public User findUser(UserId id) {
        return users.get(id);
    }

    @Override
    public void addUser(User user) {
        users.put(user.getId(), user);
    }


}
