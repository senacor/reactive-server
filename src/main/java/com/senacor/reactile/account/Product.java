package com.senacor.reactile.account;

import com.senacor.reactile.IdObject;
import com.senacor.reactile.customer.CustomerId;

public interface Product {

    CustomerId getCustomerId();
    IdObject getId();
    Type getType();


    public enum Type {
        ACCOUNT, CREDITCARD
    }
}
