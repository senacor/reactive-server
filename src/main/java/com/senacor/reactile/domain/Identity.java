package com.senacor.reactile.domain;

public interface Identity<T extends IdObject> extends Jsonizable {

    T getId();

}
