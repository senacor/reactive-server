package com.senacor.reactile;

import java.util.Set;

public interface ServiceIdProvider {


    String getId();

    Set<? extends ServiceIdProvider> dependsOn();

    default boolean hasDependencies() {
        return !dependsOn().isEmpty();
    }
}
