package com.senacor.reactile;

import java.util.Comparator;
import java.util.Set;

public interface ServiceIdProvider {

    String getId();

    Set<ServiceIdProvider> dependsOn();

    default boolean hasDependencies() {
        return !dependsOn().isEmpty();
    }

    int order();

    static Comparator<ServiceIdProvider> comparator() {
        return Comparator.comparing(service -> service.order());
    }
}
