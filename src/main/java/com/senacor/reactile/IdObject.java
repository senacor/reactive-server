package com.senacor.reactile;

public interface IdObject extends ValueObject {

    String getId();

    default String toValue() {
        return getId();
    }
}
