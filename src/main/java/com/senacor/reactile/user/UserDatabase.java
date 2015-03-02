package com.senacor.reactile.user;

import com.google.common.collect.ImmutableMap;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase {

    private final Map<UserId, User> users;

    {
        Map<UserId, User> initialUsers = ImmutableMap.<UserId, User>builder()
                .put(user("momann", "Michael", "Omann"))
                .put(user("rwinzinger", "Ralph", "Winzinger"))
                .put(user("mmenzel", "Michael", "Menzel"))
                .put(user("aloch", "Andreas", "Loch"))
                .put(user("adick", "Andreas", "Dick"))
                .put(user("cstar", "Cinnamon", "Star"))
                .put(user("aangel", "Aurora", "Angel"))
                .put(user("cross", "Crystal", "Ross"))
                .build();
        users = new ConcurrentHashMap<>(initialUsers);
    }

    private static Map.Entry<UserId, User> user(String shortName, String fistName, String lastName) {
        UserId id = new UserId(shortName);
        return new AbstractMap.SimpleEntry<>(id, new User(id, fistName, lastName));
    }


    public User login(UserId id) {
        return users.get(id);
    }

    public User findUser(UserId id) {
        return users.get(id);
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }


}
