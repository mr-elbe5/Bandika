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
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public interface IJsonData {

    String classKey = "$className";

    static IJsonData createIJsonData(JSONObject jo){
        IJsonData data = null;
        try {
            String clsName = jo.get(IJsonData.classKey).toString();
            Class<?> cls = Class.forName(clsName);
            //System.out.println(cls);
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
                    jo.put(field.getName(), getJSONObject(field));
                }
            }
            cls=cls.getSuperclass();
        }
        return jo;
    }

    default Object getJSONObject(Field field){
        try {
            Object fieldObject = field.get(this);
            if (fieldObject instanceof IJsonData) {
                return ((IJsonData) fieldObject).toJSONObject();
            }
            if (fieldObject instanceof List<?> && field.isAnnotationPresent(JsonField.class)) {
                JSONArray array = new JSONArray();
                List<?> list = (List<?>)fieldObject;
                for (Object elem : list ){
                    if (elem instanceof IJsonData) {
                        array.put(((IJsonData)elem).toJSONObject());
                    }
                }
                return array;
            }
            if (fieldObject instanceof Set<?> && field.isAnnotationPresent(JsonSet.class)) {
                JSONArray array = new JSONArray();
                Set<?> set = (Set<?>)fieldObject;
                for (Object elem : set ){
                    if (elem instanceof IJsonData) {
                        array.put(((IJsonData)elem).toJSONObject());
                    }
                    else{
                        array.put(elem);
                    }
                }
                return array;
            }
            if (fieldObject instanceof Map<?,?> && field.isAnnotationPresent(JsonField.class)) {
                JsonField annotation = field.getAnnotation(JsonField.class);
                JSONObject mapObject = new JSONObject();
                Map<?, ?> map = (Map<?, ?>) fieldObject;
                if (annotation.keyClass().equals(String.class)) {
                    for (Object key : map.keySet()) {
                        if (!(key instanceof String))
                            continue;
                        Object value = map.get(key);
                        if (value instanceof IJsonData) {
                            mapObject.put((String) key, ((IJsonData) value).toJSONObject());
                        }
                    }
                }
                else if (annotation.keyClass().equals(Integer.class)) {
                    for (Object key : map.keySet()) {
                        if (!(key instanceof Integer))
                            continue;
                        Object value = map.get(key);
                        if (value instanceof IJsonData) {
                            mapObject.put(key.toString(), ((IJsonData) value).toJSONObject());
                        }
                    }

                }
                return mapObject;
            }
            return fieldObject;
        } catch (IllegalAccessException e) {
            System.out.println("unable to serialize field - " + e.getMessage());
        }
        return null;
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
        //System.out.println("field name = " + field.getName());
        Object fieldObject = jo.opt(field.getName());
        if (fieldObject != null) {
            try {
                Class<?> fieldType = field.getType();
                if (fieldType.equals(LocalDateTime.class)) {
                    field.set(this, LocalDateTime.parse(fieldObject.toString()));
                } else if (fieldType.equals(LocalDate.class)) {
                    field.set(this, LocalDate.parse(fieldObject.toString()));
                } else if (fieldType.equals(List.class) && field.isAnnotationPresent(JsonField.class) && fieldObject instanceof JSONArray) {
                    JSONArray array = (JSONArray) fieldObject;
                    JsonField annotation = field.getAnnotation(JsonField.class);
                    Class valueClass = annotation.valueClass();
                    field.set(this, getDataList(array, valueClass));
                } else if (fieldType.equals(Set.class) && field.isAnnotationPresent(JsonSet.class) && fieldObject instanceof JSONArray) {
                    JSONArray array = (JSONArray) fieldObject;
                    JsonSet annotation = field.getAnnotation(JsonSet.class);
                    Class valueClass = annotation.valueClass();
                    field.set(this, getSet(array, valueClass));
                } else if (fieldType.equals(Map.class) && field.isAnnotationPresent(JsonField.class) && fieldObject instanceof JSONObject) {
                    JSONObject mapObj = (JSONObject) fieldObject;
                    JsonField annotation = field.getAnnotation(JsonField.class);
                    Class valueClass = annotation.valueClass();
                    if (annotation.keyClass().equals(String.class)){
                        field.set(this, getStringDataMap(mapObj, valueClass));
                    }
                    else if (annotation.keyClass().equals(Integer.class)){
                        field.set(this, getIntDataMap(mapObj, valueClass));
                    }
                } else if (fieldObject instanceof JSONObject) {
                    IJsonData no = createIJsonData((JSONObject) fieldObject);
                    field.set(this, no);
                } else {
                    field.set(this, fieldObject);
                }
            } catch (Exception e) {
                System.out.println("unable to deserialize field - " + e.getMessage());
            }
        }
    }

    default <T extends IJsonData> List<T> getDataList(JSONArray array, Class<T> valueClass){
        List<T> list = new ArrayList<>();
        for (Object jo : array){
            list.add(valueClass.cast(createIJsonData((JSONObject) jo)));
        }
        return list;
    }

    default <T> Set<T> getSet(JSONArray array, Class<T> cls){
        Set<T> set = new HashSet<>();
        for (Object jo : array){
            set.add(cls.cast(jo));
        }
        return set;
    }

    default <T extends IJsonData> Map<String,T> getStringDataMap(JSONObject mapObj, Class<T> valueClass){
        Map<String,T> map = new HashMap<>();
        for (String key : mapObj.keySet()){
            JSONObject njo = (JSONObject) mapObj.get(key);
            map.put(key,valueClass.cast(createIJsonData(njo)));
        }
        return map;
    }

    default <T extends IJsonData> Map<Integer,T> getIntDataMap(JSONObject mapObj, Class<T> valueClass){
        Map<Integer,T> map = new HashMap<>();
        for (String key : mapObj.keySet()){
            JSONObject njo = (JSONObject) mapObj.get(key);
            map.put(Integer.parseInt(key),valueClass.cast(createIJsonData(njo)));
        }
        return map;
    }

    default void dump(){
        JSONObject obj = toJSONObject();
        Log.log(obj.toString(2));
    }
    
}
