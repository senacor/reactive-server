package com.senacor.reactile.domain;

import com.senacor.reactile.json.Jsonizable;

public interface Identity<T extends IdObject> extends Jsonizable {

    T getId();

}
