package com.senacor.reactile.service.account;

import com.senacor.reactile.IdObject;
import com.senacor.reactile.service.customer.CustomerId;

public interface Product {

    CustomerId getCustomerId();
    IdObject getId();
    Type getType();


    enum Type {
        ACCOUNT, CREDITCARD
    }
}
