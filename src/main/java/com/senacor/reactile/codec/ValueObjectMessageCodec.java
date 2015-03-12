package com.senacor.reactile.codec;

import com.senacor.reactile.ValueObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.impl.codecs.StringMessageCodec;

public class ValueObjectMessageCodec<T extends ValueObject> implements MessageCodec<T, T> {
    private final Class<T> clazz;

    private final StringMessageCodec delegate = new StringMessageCodec();

    public ValueObjectMessageCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static final <T extends ValueObject> ValueObjectMessageCodec from(Class<T> clazz) {
        return new ValueObjectMessageCodec<>(clazz);
    }


    @Override
    public void encodeToWire(Buffer buffer, T o) {
        delegate.encodeToWire(buffer, o.toValue());
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        T obj;

        try {
            obj = clazz.getConstructor(String.class).newInstance(delegate.decodeFromWire(pos, buffer));
        } catch (Exception e) {
            throw new RuntimeException("decodeFromWire/constructor failed: "+e.getMessage());
        }

        return obj;
    }

    @Override
    public T transform(T object) {
        return object;
    }

    @Override
    public String name() {
        return "string-" + clazz.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -2;
    }
}
