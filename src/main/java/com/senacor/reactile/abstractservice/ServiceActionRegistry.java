package com.senacor.reactile.abstractservice;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stores a map of methods that are exposed as action by a service class
 */
public class ServiceActionRegistry {

    static class ActionMetaData{
        private Method method;
        private Action.MessagePattern pattern;

        public ActionMetaData(Method method, Action.MessagePattern pattern) {
            this.method = method;
            this.pattern = pattern;
        }

        public Method getMethod() {
            return method;
        }

        public Action.MessagePattern getPattern() {
            return pattern;
        }
    }

    private final Map<String, ActionMetaData> actions;

    public ServiceActionRegistry(Map<String, ActionMetaData> actions) {
        this.actions = actions;
    }

    boolean hasAction(String action){
        return actions.containsKey(action);
    }

    Method getAction(String action) {
        if (!hasAction(action)) {
            throw new IllegalArgumentException("Unknown abstractService operation " + action);
        }
        return actions.get(action).method;
    }

    List<Method> getActionsForPattern(Action.MessagePattern pattern) {

        return actions.values().stream()
                .filter(metaData -> metaData.getPattern().equals(pattern))
                .map(metaData -> metaData.getMethod())
                .collect(Collectors.toList());
    }

    static ServiceActionRegistry getFromClass(Class<?> clazz) {
        return new ServiceActionRegistry(
                Arrays.stream(clazz.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Action.class))
                        .collect(Collectors.toMap(methodToActionNameMapper(), method -> getMetaDataFromMethod(method))));
    }

    private static ActionMetaData getMetaDataFromMethod(Method method) {
        Action.MessagePattern pattern = method.getAnnotation(Action.class).pattern();
        return new ActionMetaData(method, pattern);
    }

    private static Function<Method, String> methodToActionNameMapper() {
        return method -> {
            Action actionAnnot = method.getAnnotation(Action.class);
            String actionName = actionAnnot.name();
            if (actionName.equals(Action.METHOD_NAME)) {
                return method.getName();
            }
            return actionName;
        };
    }

}
