/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package de.elbe5.companion;

import de.elbe5.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public interface JsonCompanion {

    default Object toJson(Object obj){
        if (obj instanceof String)
            return obj;
        if (obj instanceof Integer || obj instanceof Long || obj instanceof Float
                || obj instanceof Double || obj instanceof Boolean || obj instanceof LocalDate
                || obj instanceof LocalTime || obj instanceof LocalDateTime)
            return obj.toString();
        if (obj.getClass().isEnum()){
            return ((Enum<?>)obj).name();
        }
        if (obj instanceof List<?>){
            JSONArray array = new JSONArray();
            for (Object elem : (List<?>)obj){
                Object jelem = toJson(elem);
                if (jelem != null) {
                    array.put(jelem);
                }
            }
            return array;
        }
        if (obj instanceof Set<?>){
            JSONArray array = new JSONArray();
            for (Object elem : (Set<?>)obj){
                Object jelem = toJson(elem);
                if (jelem != null) {
                    array.put(jelem);
                }
            }
            return array;
        }
        if (obj instanceof Map<?,?>){
            JSONObject jobj = new JSONObject();
            Map<?,?> map = (Map<?,?>)obj;
            for (Object key : map.keySet()){
                Object value = map.get(key);
                String jkey = key.toString();
                Object jvalue = toJson(value);
                if (jkey != null && jvalue != null) {
                    jobj.put(jkey, jvalue);
                }
            }
            return jobj;
        }
        return null;
    }

    default Object fromJson(Object obj, Class<?> baseClass){
        if (obj instanceof Integer){
            if (baseClass.equals(Long.class)){
                return baseClass.cast(obj);
            }
            return obj;
        }
        if (obj instanceof Double){
            if (baseClass.equals(Float.class)){
                return baseClass.cast(obj);
            }
            return obj;
        }
        if (obj instanceof Boolean){
            return obj;
        }
        if (obj instanceof String){
            return fromJsonString((String)obj, baseClass);
        }
        if (obj instanceof JSONObject){
            return fromJsonObject((JSONObject) obj, baseClass);
        }
        return null;
    }

    default Object fromJson(Object obj, Class<?> baseClass, Class<?> valueClass){
        if (obj instanceof JSONObject){
            return fromJsonObject((JSONObject) obj, baseClass, valueClass);
        }
        if (obj instanceof JSONArray){
            return fromJsonArray((JSONArray) obj, baseClass, valueClass);
        }
        return null;
    }

    default Object fromJson(Object obj, Class<?> baseClass, Class<?> keyClass, Class<?> valueClass){
        if (obj instanceof JSONObject){
            return fromJsonObject((JSONObject) obj, baseClass, keyClass, valueClass);
        }
        return null;
    }

    default Object fromJsonString(String str, Class<?> cls){
        if (cls.equals(String.class)){
            return str;
        }
        if (cls.equals(Integer.class)){
            try{
                return Integer.parseInt(str);
            }
            catch (NumberFormatException e){
                Log.error("bad number format", e);
                return 0;
            }
        }
        if (cls.equals(Long.class)){
            try{
                return Long.parseLong(str);
            }
            catch (NumberFormatException e){
                Log.error("bad number format", e);
                return 0;
            }
        }
        if (cls.equals(Boolean.class)){
            return Boolean.parseBoolean(str);
        }
        if (cls.equals(LocalDate.class)){
            try{
                return LocalDate.parse(str);
            }
            catch (DateTimeParseException e){
                Log.error("bad date format", e);
                return null;
            }
        }
        if (cls.equals(LocalTime.class)){
            try{
                return LocalTime.parse(str);
            }
            catch (DateTimeParseException e){
                Log.error("bad date format", e);
                return null;
            }
        }
        if (cls.equals(LocalDateTime.class)){
            try{
                return LocalDateTime.parse(str);
            }
            catch (DateTimeParseException e){
                Log.error("bad date format", e);
                return null;
            }
        }
        return null;
    }

    default Object fromJsonObject(JSONObject jobj, Class<?> cls) {
        return null;
    }

    default Object fromJsonObject(JSONObject jobj, Class<?> baseClass, Class<?> valueClass) {
        if (baseClass.equals(Map.class)) {
            return getMap(jobj, String.class, valueClass);
        }
        return null;
    }

    default Object fromJsonObject(JSONObject jobj, Class<?> baseClass, Class<?> keyClass, Class<?> valueClass) {
        if (baseClass.equals(Map.class)) {
            return getMap(jobj, keyClass, valueClass);
        }
        return null;
    }

    default Object fromJsonArray(JSONArray jarr, Class<?> baseClass, Class<?> valueClass) {
        if (baseClass.equals(List.class)) {
            return getList(jarr, valueClass);
        }
        if (baseClass.equals(Set.class)) {
            return getSet(jarr, valueClass);
        }
        return null;
    }

    default <T> List<T> getList(JSONArray array, Class<T> valueClass){
        List<T> list = new ArrayList<>();
        for (Object jo : array){
            try {
                list.add(valueClass.cast(jo));
            }
            catch (ClassCastException e){
                Log.error("could not cast object", e);
            }
        }
        return list;
    }

    default <T> Set<T> getSet(JSONArray array, Class<T> cls){
        Set<T> set = new HashSet<>();
        for (Object jo : array){
            try{
                set.add(cls.cast(jo));
            }
            catch (ClassCastException e){
                Log.error("could not cast object", e);
            }
        }
        return set;
    }

    default <K,T> Map<K,T> getMap(JSONObject jobj, Class<K> keyClass, Class<T> valueClass){
        Map<K,T> map = new HashMap<>();
        for (String jkey : jobj.keySet()){
            Object jvalue = jobj.get(jkey);
            try{
                K key = keyClass.cast(fromJson(jkey, keyClass));
                T value = valueClass.cast(fromJson(jvalue, valueClass));
                map.put(key, value);
            }
            catch (ClassCastException e){
                Log.error("could not cast object", e);
            }
        }
        return map;
    }
    
}
