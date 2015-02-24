package com.senacor.reactile.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.io.IOException;

public class DomainObjectMessageCodec<T> implements MessageCodec<T, T> {

    private final static ObjectMapper om = new ObjectMapper();

    {
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        om.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        om.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        //allow objects without properties
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private final Class<T> clazz;

    private DomainObjectMessageCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static final <T> DomainObjectMessageCodec from(Class<T> clazz) {
        return new DomainObjectMessageCodec<>(clazz);
    }


    @Override
    public void encodeToWire(Buffer buffer, T o) {
        try {
            buffer.appendBytes(om.writeValueAsBytes(o));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        try {
            return om.readValue(buffer.getBytes(pos, length), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T transform(T object) {
        return object;
    }

    @Override
    public String name() {
        return "json-" + clazz.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
