package com.senacor.reactile.codec;

import com.senacor.reactile.ValueObject;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.impl.codecs.JsonArrayMessageCodec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

public class ArrayListObjectMessageCodec<E> implements MessageCodec<ArrayList<E>, ArrayList<E>> {
    private enum Type {
        JSONIZABLE("JSONIZABLE"),
        VALUEOBJECT("VALUEOBJECT"),
        EMPTY("EMPTY");

        private final String name;

        Type(String name) {
            this.name = name;
        }
    }

    private final JsonArrayMessageCodec delegate = new JsonArrayMessageCodec();

    @Override
    public void encodeToWire(Buffer buffer, ArrayList<E> list) {
        Type type;
        Object firstElem = null;
        if (list.size() == 0) {
            type = Type.EMPTY;
        } else {
            firstElem = list.get(0);
            if (Jsonizable.class.isInstance(firstElem)) {
                type = Type.JSONIZABLE;
            } else if (ValueObject.class.isInstance(firstElem)) {
                type = Type.VALUEOBJECT;
            } else {
                throw new RuntimeException("no codec found");
            }
        }

        JsonArray jsonArray = new JsonArray();
        if (type == Type.EMPTY) {
            jsonArray.add(new JsonObject().put("listtype", type.name).put("class", "n/a"));
        } else {
            jsonArray.add(new JsonObject().put("listtype", type.name).put("class", firstElem.getClass().getName()));
            for (E obj : list) {
                if (type == Type.JSONIZABLE) {
                    jsonArray.add(((Jsonizable) obj).toJson());
                } else {
                    jsonArray.add(new JsonObject().put("value", ((ValueObject) obj).toValue()));
                }
            }
        }

        System.out.println("jsonArray = " + jsonArray);
        delegate.encodeToWire(buffer, jsonArray);
    }

    @Override
    public ArrayList<E> decodeFromWire(int pos, Buffer buffer) {
        JsonArray jsonArray = delegate.decodeFromWire(pos, buffer);

        Type type = Type.valueOf(jsonArray.getJsonObject(0).getString("listtype"));
        if (type == Type.EMPTY) return new ArrayList<>();

        String className = jsonArray.getJsonObject(0).getString("class");

        ArrayList<E> resultList = new ArrayList<>();
        for (int i=1; i<jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            try {
                if (type == Type.JSONIZABLE) {
                    resultList.add((E) Class.forName(className).getMethod("fromJson", JsonObject.class).invoke(null, jsonObject));
                } else {
                    resultList.add((E) Class.forName(className).getConstructor(String.class).newInstance(jsonObject.getString("value")));
                }
            } catch (Exception e) {
                throw new RuntimeException("decodeFromWire/Class.forName failed: " + e.getMessage());
            }
        }

        return resultList;
    }

    @Override
    public ArrayList<E> transform(ArrayList<E> list) {
        return new ArrayList<>(list);
    }

    @Override
    public String name() {
        return "arraylist";
    }

    @Override
    public byte systemCodecID() {
        return -3;
    }
}
