package com.senacor.reactile.codec;

import com.senacor.reactile.customer.CustomerId;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

public class DomainObjectMessageCodecTest {

    private final DomainObjectMessageCodec codec = DomainObjectMessageCodec.from(CustomerId.class);

    @Test
    public void thatCustomerIdCanBeEnCoded() {
        Buffer buffer = Buffer.buffer();
        codec.encodeToWire(buffer, new CustomerId("0815"));
    }

}