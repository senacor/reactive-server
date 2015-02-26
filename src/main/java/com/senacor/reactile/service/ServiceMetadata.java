package com.senacor.reactile.service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServiceMetadata {
    private final Map<String, Method> actions;

    public ServiceMetadata(Map<String, Method> actions) {
        this.actions = actions;
    }

    boolean hasAction(String action){
        return action.contains(action);
    }

    Method getAction(String action) {
        return actions.get(action);
    }

    Set<String> getActions() {
        return new HashSet<>(actions.keySet());
    }

}
