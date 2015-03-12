package com.senacor.reactile;

import java.util.Collections;
import java.util.Set;

public interface ServiceIdProvider {


    String getId();

    default Set<? extends ServiceIdProvider> dependsOn() {
        return Collections.emptySet();
    }
}
