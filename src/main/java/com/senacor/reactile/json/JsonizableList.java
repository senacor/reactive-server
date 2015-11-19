package com.senacor.reactile.json;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Michael Menzel
 */
public class JsonizableList<T> implements Jsonizable {

    private final List<T> items;

    public JsonizableList() {
        items = new LinkedList<T>();
    }

    public JsonizableList(JsonizableList jsonizableListList) {
        items = jsonizableListList.toList();
    }

    public JsonizableList(List<T> list) {
        items = list;
    }

    public JsonizableList(JsonObject jsonObject) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        items = new LinkedList<T>();
        String typeName = jsonObject.getString("type");
        if(null == jsonObject || typeName == null) {
            return;
        }

        Class<?> type = Class.forName(typeName);
        JsonArray jsonItems = jsonObject.getJsonArray("items");

        for(int i = 0; i < jsonItems.size(); i++){
            if(Jsonizable.class.isAssignableFrom(type)) {
                items.add((T)type.getConstructor(JsonObject.class).newInstance(jsonItems.getJsonObject(i)));
            } else if(String.class.isAssignableFrom(type)) {
                items.add((T)jsonItems.getString(i));
            } else if(Long.class.isAssignableFrom(type)) {
                items.add((T)jsonItems.getLong(i));
            } else if(Integer.class.isAssignableFrom(type)) {
                items.add((T)jsonItems.getInteger(i));
            } else if(Boolean.class.isAssignableFrom(type)) {
                items.add((T)jsonItems.getBoolean(i));
            } else if(Double.class.isAssignableFrom(type)) {
                items.add((T)jsonItems.getDouble(i));
            }  else if(JsonObject.class.isAssignableFrom(type)) {
                items.add((T)jsonItems.getJsonObject(i));
            } else if(JsonArray.class.isAssignableFrom(type)) {
                items.add((T)jsonItems.getJsonArray(i));
            } else {
                throw new RuntimeException("Return Type does not implement Jsonizable");
            }
        };
    }

    public List<T> toList() {
        return items;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonItemArray = new JsonArray();

        jsonObject.put("items", jsonItemArray);

        if(items.size()>0){
            jsonObject.put("type", this.items.get(0).getClass().getName());
        }

        for (Object item: this.items){
            if (item instanceof Jsonizable) {
                jsonItemArray.add(((Jsonizable)item).toJson());
            } else {
                jsonItemArray.add(item);
            };
        }

            return jsonObject;
    }

}
