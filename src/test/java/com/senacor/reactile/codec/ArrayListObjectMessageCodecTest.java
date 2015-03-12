package com.senacor.reactile.codec;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class ArrayListObjectMessageCodecTest {

    private final ArrayListObjectMessageCodec codec = new ArrayListObjectMessageCodec();

    @Test
    public void thatArrayListOfDomainObjectCanBeEnCoded() {
        Buffer buffer = Buffer.buffer();
        codec.encodeToWire(buffer, getArrayListOfDomainObject());
        ArrayList res = codec.decodeFromWire(0, buffer);
        System.out.println("res = " + res);
    }

    @Test
    public void blabla() {
        ArrayList<Customer> list = new ArrayList<>();
        Class listType = (Class) ((ParameterizedType)list.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    private ArrayList<Customer> getArrayListOfDomainObject() {
        ArrayList<Customer> result = new ArrayList<>();

        for (int i=0; i<10; i++) {
            result.add(Customer.newBuilder().withId("c-" + i).withFirstname("Hans_" + i).withLastname("Dampf_" + i).withTaxCountry(new Country("DE", "DE")).build());
        }

        return result;
    }
}