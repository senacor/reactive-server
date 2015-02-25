package com.senacor.reactile;

public interface IdObject {

    String getId();

    default String toValue() {
        return getId();
    }

}
