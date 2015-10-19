package com.senacor.reactile.codec;

import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerFixtures;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

public class DomainObjectMessageCodecTest {

    private final DomainObjectMessageCodec codec = DomainObjectMessageCodec.from(Customer.class);

    @Test
    public void thatCustomerCanBeEnCoded() {
        Buffer buffer = Buffer.buffer();
        codec.encodeToWire(buffer, CustomerFixtures.defaultCustomer());
    }

}