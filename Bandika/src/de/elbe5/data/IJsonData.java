/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package de.elbe5.data;

import de.elbe5.log.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public interface IJsonData {

    String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    String classKey = "$className";

    static IJsonData createFromJSONObject(JSONObject jo){
        IJsonData data = null;
        try {
            String clsName = jo.get(IJsonData.classKey).toString();
            Class<?> cls = Class.forName(clsName);
            System.out.println(cls);
            Object obj = cls.getDeclaredConstructor().newInstance();
            if (obj instanceof IJsonData) {
                data = (IJsonData) obj;
                data.fromJSONObject(jo);
            }
        }
        catch (Exception e){
            System.out.println("could not create from JSON " + e.getMessage());
        }
        return data;
    }

    // default methods

    default String getJSONString() {
        return toJSONObject().toString(2);
    }
    default JSONObject toJSONObject() {
        Class<?> cls = getClass();
        JSONObject jo = new JSONObject();
        jo.put(IJsonData.classKey, cls.getName());
        while (cls!=null && cls.isAnnotationPresent(JsonClass.class)){
            for (Field field : cls.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(JsonField.class)) {
                    addToJSONObject(jo, field);
                }
            }
            cls=cls.getSuperclass();
        }
        return jo;
    }

    default void addToJSONObject(JSONObject jo, Field field){
        try {
            Object fieldObject = field.get(this);
            if (fieldObject instanceof IJsonData) {
                jo.put(field.getName(), ((IJsonData) fieldObject).toJSONObject());
            } else {
                jo.put(field.getName(), fieldObject);
            }
        } catch (IllegalAccessException e) {
            System.out.println("unable to serialize field" + e.getMessage());
        }
    }

    default void fromJSONObject(JSONObject jo){
        Class<?> cls = getClass();
        while (cls!=null && cls.isAnnotationPresent(JsonClass.class)){
            for (Field field : cls.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(JsonField.class)) {
                    readFromJsonObject(jo, field);
                }
            }
            cls=cls.getSuperclass();
        }
    }

    default void readFromJsonObject(JSONObject jo, Field field){
        System.out.println("field name = " + field.getName());
        Object fieldObject = jo.opt(field.getName());
        if (fieldObject != null) {
            try {
                System.out.println("field type = " + field.getType());
                System.out.println("field object type = " + fieldObject.getClass());
                if (fieldObject instanceof JSONObject) {
                    IJsonData no = createFromJSONObject((JSONObject) fieldObject);
                    field.set(this, no);
                } else if (field.getType() == LocalDateTime.class) {
                    field.set(this, LocalDateTime.parse(fieldObject.toString()));
                } else if (field.getType() == LocalDate.class) {
                    field.set(this, LocalDate.parse(fieldObject.toString()));
                } else {
                    field.set(this, fieldObject);
                }
            } catch (IllegalAccessException e) {
                System.out.println("unable to deserialize field" + e.getMessage());
            }
        }
    }

    // JsonData

    default boolean fromParentObject(JSONObject parentObject, String name){
        try {
            JSONObject obj = parentObject.optJSONObject(name);
            if (obj ==null){
                return false;
            }
            fromJSONObject(obj);
            return true;
        } catch (JSONException | NullPointerException e){
            Log.error("could not get json from parent object: "+name, e);
            return false;
        }
    }

    // Lists

    default <T extends IJsonData> JSONArray createJSONArray(List<T> list){
        JSONArray arr = new JSONArray();
        for (T data : list){
            arr.put(data.toJSONObject());
        }
        return arr;
    }

    default <T extends IJsonData> List<T> getList(JSONObject obj, String key, Class<T> listClass){
        List<T> list = new ArrayList<>();
        try{
            if (obj.has(key)) {
                JSONArray arr = obj.optJSONArray(key);
                if (arr == null){
                    return list;
                }
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject itemObj = obj.optJSONObject(key);
                    String typeKey = itemObj.optString(IJsonData.classKey);
                    //todo
                    /* (data!=null) {
                        data.fromJSONObject(itemObj);
                        list.add(data);
                    }*/
                }
            }
        }catch (JSONException e) {
            Log.error("could not read json array", e);
        }
        return list;
    }

    // Maps

    default <T extends IJsonData> JSONObject createJSONObjectFromIntMap(Map<Integer,T> map){
        JSONObject obj = new JSONObject();
        for (Integer i : map.keySet()){
            obj.put(i.toString(),map.get(i).toJSONObject());
        }
        return obj;
    }

    default <T extends IJsonData> JSONObject createJSONObjectFromStringMap(Map<String,T> map){
        JSONObject obj = new JSONObject();
        for (String key : map.keySet()){
            obj.put(key,map.get(key).toJSONObject());
        }
        return obj;
    }

    default void dump(){
        JSONObject obj = toJSONObject();
        Log.log(obj.toString(2));
    }
}
