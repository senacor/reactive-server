package com.senacor.reactile;

import com.senacor.reactile.domain.Jsonizable;

public interface Identity<T extends IdObject> extends Jsonizable {

    T getId();

}
