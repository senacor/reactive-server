package com.senacor.reactile.codec;

import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.impl.codecs.JsonObjectMessageCodec;

public class DomainObjectMessageCodec<T extends Jsonizable> implements MessageCodec<T, T> {
    private final Class<T> clazz;

    private final JsonObjectMessageCodec delegate = new JsonObjectMessageCodec();

    public DomainObjectMessageCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static final <T extends Jsonizable> DomainObjectMessageCodec from(Class<T> clazz) {
        return new DomainObjectMessageCodec<>(clazz);
    }

    @Override
    public void encodeToWire(Buffer buffer, T o) {
        delegate.encodeToWire(buffer, o.toJson());
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        T obj;

        try {
            obj = (T) clazz.getMethod("fromJson").invoke(null, delegate.decodeFromWire(pos, buffer));
        } catch (Exception e) {
            throw new RuntimeException("decodeFromWire/fromJson failed: "+e.getMessage());
        }

        return obj;
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
