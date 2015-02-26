package com.senacor.reactile.service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActionInspector {

    static ServiceMetadata getServiceMetadata(Class<?> clazz) {
        return new ServiceMetadata(
                Arrays.stream(clazz.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Action.class))
                        .collect(Collectors.toMap(keyMapper(), method -> method)));
    }

    private static Function<Method, String> keyMapper() {
        return method -> {
            Action actionAnnot = method.getAnnotation(Action.class);
            String actionName = actionAnnot.value();
            if (actionName.equals(Action.METHOD_NAME)) {
                return method.getName();
            }
            return actionName;
        };
    }
}
