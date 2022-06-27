/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package de.elbe5.companion;

import de.elbe5.data.JsonClass;
import de.elbe5.data.JsonField;
import de.elbe5.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public interface JsonCompanion {

    String classKey = "$className";

    default JSONObject toJSONObject() {
        Class<?> cls = getClass();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonCompanion.classKey, cls.getName());
        while (cls!=null && cls.isAnnotationPresent(JsonClass.class)){
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonField.class)){
                    field.setAccessible(true);
                    Class<?> fieldClass = field.getType();
                    try{
                        if (fieldClass.isPrimitive() || fieldClass.equals(String.class)){
                            jsonObject.put(field.getName(), field.get(this));
                        }
                        else {
                            Object fieldValue = field.get(this);
                            if (fieldValue != null) {
                                Object value = toJson(fieldValue);
                                jsonObject.put(field.getName(), value);
                            }
                        }
                    }
                    catch (Exception e){
                        Log.error("could not add field value to json object", e);
                    }
                }
            }
            cls=cls.getSuperclass();
        }
        return jsonObject;
    }

    default void fromJSONObject(JSONObject jobj){
        Class<?> cls = getClass();
        while (cls!=null && cls.isAnnotationPresent(JsonClass.class)){
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonField.class)) {
                    Object jsonValue = jobj.opt(field.getName());
                    if (jsonValue != null) {
                        Object obj;
                        JsonField annotation = field.getAnnotation(JsonField.class);
                        field.setAccessible(true);
                        if (!annotation.valueClass().equals(Object.class)){
                            if (!annotation.keyClass().equals(Object.class)){
                                obj = fromJson(jsonValue, annotation.baseClass(), annotation.keyClass(), annotation.valueClass());
                            }
                            else{
                                obj = fromJson(jsonValue, annotation.baseClass(), annotation.valueClass());
                            }
                        }
                        else{
                            obj = fromJson(jsonValue, annotation.baseClass());
                        }
                        if (obj != null) {
                            try {
                                field.set(this, obj);
                            } catch (Exception e) {
                                Log.error("could not get object from json", e);
                            }
                        }
                    }
                }
            }
            cls=cls.getSuperclass();
        }
    }

    default Object toJson(Object value){
        if (value instanceof String)
            return value;
        if (value instanceof Integer || value instanceof Long || value instanceof Float
                || value instanceof Double || value instanceof Boolean)
            return value;
        if (value instanceof LocalDate || value instanceof LocalTime || value instanceof LocalDateTime)
            return value.toString();
        if (value.getClass().isEnum()){
            return ((Enum<?>)value).name();
        }
        if (value instanceof List<?>){
            JSONArray jsonArray = new JSONArray();
            for (Object elem : (List<?>)value){
                Object jelem = toJson(elem);
                if (jelem != null) {
                    jsonArray.put(jelem);
                }
            }
            return jsonArray;
        }
        if (value instanceof Set<?>){
            JSONArray jsonArray = new JSONArray();
            for (Object elem : (Set<?>)value){
                Object jelem = toJson(elem);
                if (jelem != null) {
                    jsonArray.put(jelem);
                }
            }
            return jsonArray;
        }
        if (value instanceof Map<?,?>){
            JSONObject jsonObject = new JSONObject();
            Map<?,?> map = (Map<?,?>)value;
            for (Object mapKey : map.keySet()){
                Object mapValue = map.get(mapKey);
                String jsonKey = mapKey.toString();
                Object jasonValue = toJson(mapValue);
                if (jsonKey != null && jasonValue != null) {
                    jsonObject.put(jsonKey, jasonValue);
                }
            }
            return jsonObject;
        }
        return null;
    }

    default Object fromJson(Object jasonValue, Class<?> baseClass){
        if (jasonValue instanceof Integer){
            if (baseClass.equals(Long.class)){
                return baseClass.cast(jasonValue);
            }
            return jasonValue;
        }
        if (jasonValue instanceof Double){
            if (baseClass.equals(Float.class)){
                return baseClass.cast(jasonValue);
            }
            return jasonValue;
        }
        if (jasonValue instanceof Boolean){
            return jasonValue;
        }
        if (jasonValue instanceof String){
            return fromJsonString((String)jasonValue, baseClass);
        }
        if (jasonValue instanceof JSONObject){
            return fromJsonObject((JSONObject) jasonValue, baseClass);
        }
        return null;
    }

    default Object fromJson(Object jasonValue, Class<?> baseClass, Class<?> valueClass){
        if (jasonValue instanceof JSONObject){
            return fromJsonObject((JSONObject) jasonValue, baseClass, valueClass);
        }
        if (jasonValue instanceof JSONArray){
            return fromJsonArray((JSONArray) jasonValue, baseClass, valueClass);
        }
        return null;
    }

    default Object fromJson(Object jasonValue, Class<?> baseClass, Class<?> keyClass, Class<?> valueClass){
        if (jasonValue instanceof JSONObject){
            return fromJsonObject((JSONObject) jasonValue, baseClass, keyClass, valueClass);
        }
        return null;
    }

    default Object fromJsonString(String jasonValue, Class<?> baseClass){
        if (baseClass.equals(String.class)){
            return jasonValue;
        }
        if (baseClass.equals(Integer.class)){
            try{
                return Integer.parseInt(jasonValue);
            }
            catch (NumberFormatException e){
                Log.error("bad number format", e);
                return 0;
            }
        }
        if (baseClass.equals(Long.class)){
            try{
                return Long.parseLong(jasonValue);
            }
            catch (NumberFormatException e){
                Log.error("bad number format", e);
                return 0;
            }
        }
        if (baseClass.equals(Boolean.class)){
            return Boolean.parseBoolean(jasonValue);
        }
        if (baseClass.equals(LocalDate.class)){
            try{
                return LocalDate.parse(jasonValue);
            }
            catch (DateTimeParseException e){
                Log.error("bad date format", e);
                return null;
            }
        }
        if (baseClass.equals(LocalTime.class)){
            try{
                return LocalTime.parse(jasonValue);
            }
            catch (DateTimeParseException e){
                Log.error("bad date format", e);
                return null;
            }
        }
        if (baseClass.equals(LocalDateTime.class)){
            try{
                return LocalDateTime.parse(jasonValue);
            }
            catch (DateTimeParseException e){
                Log.error("bad date format", e);
                return null;
            }
        }
        if (baseClass.isEnum()){
            try {
                Method method = baseClass.getDeclaredMethod("valueOf", String.class);
                return method.invoke(null, jasonValue);
            }
            catch (Exception e){
                Log.error("could not get enum ", e);
            }
        }
        return null;
    }

    default Object fromJsonObject(JSONObject jsonObject, Class<?> baseClass) {
        return null;
    }

    default Object fromJsonObject(JSONObject jsonObject, Class<?> baseClass, Class<?> valueClass) {
        if (baseClass.equals(Map.class)) {
            return getMap(jsonObject, String.class, valueClass);
        }
        return null;
    }

    default Object fromJsonObject(JSONObject jsonObject, Class<?> baseClass, Class<?> keyClass, Class<?> valueClass) {
        if (baseClass.equals(Map.class)) {
            return getMap(jsonObject, keyClass, valueClass);
        }
        return null;
    }

    default Object fromJsonArray(JSONArray jsonArray, Class<?> baseClass, Class<?> valueClass) {
        if (List.class.isAssignableFrom(baseClass)) {
            return getList(jsonArray, valueClass);
        }
        if (Set.class.isAssignableFrom(baseClass)) {
            return getSet(jsonArray, valueClass);
        }
        return null;
    }

    default <T> List<T> getList(JSONArray jsonArray, Class<T> valueClass){
        List<T> list = new ArrayList<>();
        for (Object jsonValue : jsonArray){
            try {
                list.add(valueClass.cast(fromJson(jsonValue, valueClass)));
            }
            catch (ClassCastException e){
                Log.error("could not cast object", e);
            }
        }
        return list;
    }

    default <T> Set<T> getSet(JSONArray jsonArray, Class<T> valueClass){
        Set<T> set = new HashSet<>();
        for (Object jsonValue : jsonArray){
            try{
                set.add(valueClass.cast(fromJson(jsonValue, valueClass)));
            }
            catch (ClassCastException e){
                Log.error("could not cast object", e);
            }
        }
        return set;
    }

    default <K,T> Map<K,T> getMap(JSONObject jsonObject, Class<K> keyClass, Class<T> valueClass){
        Map<K,T> map = new HashMap<>();
        for (String jsonKey : jsonObject.keySet()){
            Object jsonValue = jsonObject.get(jsonKey);
            try{
                K key = keyClass.cast(fromJson(jsonKey, keyClass));
                T value = valueClass.cast(fromJson(jsonValue, valueClass));
                map.put(key, value);
            }
            catch (ClassCastException e){
                Log.error("could not cast object", e);
            }
        }
        return map;
    }
    
}
