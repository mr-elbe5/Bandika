/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package de.elbe5.companion;

import de.elbe5.data.IJsonData;
import de.elbe5.log.Log;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public interface JsonCompanion {

    String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    default @NotNull Object deserialize(@NotNull InputStream in) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[0x4000];
        int len;
        try {
            while ((len = in.read(buffer, 0, 0x4000)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return deserialize(outputStream.toByteArray());
    }

    default @NotNull Object deserialize(byte[] bytes) throws IOException {
        if (bytes == null) {
            throw new IOException("JSON byte array cannot be null");
        }
        if (bytes.length == 0) {
            throw new IOException("Invalid JSON: zero length byte array.");
        }
        try {
            String s = new String(bytes, StandardCharsets.UTF_8);
            return new JSONParser().parse(s);
        } catch (Exception e) {
            String msg = "Invalid JSON: " + e.getMessage();
            throw new IOException(msg, e);
        }
    }

    default @NotNull String serializeObject(@NotNull Object obj) {
        try {
            Object o = toJSONInstance(obj);
            if (o instanceof JSONObject){
                return ((JSONObject) o).toJSONString();
            }
            else if (o instanceof JSONArray){
                return ((JSONArray) o).toJSONString();
            }
            return "";
        }
        catch (Exception e){
            Log.warn("Unable to serialize object");
            return "";
        }
    }

    default @NotNull Object toJSONInstance(@NotNull Object object) {
        if (object instanceof JSONObject || object instanceof JSONArray
                || object instanceof Byte || object instanceof Character
                || object instanceof Short || object instanceof Integer
                || object instanceof Long || object instanceof Boolean
                || object instanceof Float || object instanceof Double
                || object instanceof String || object instanceof BigInteger
                || object instanceof BigDecimal || object instanceof Enum) {
            return object;
        }
        if (object instanceof LocalDate) {
            object = LocalDateTime.of((LocalDate) object, LocalTime.of(0,0,0));
        }
        if (object instanceof LocalDateTime) {
            return ((LocalDateTime) object).format(DateTimeFormatter.ofPattern(ISO_8601_PATTERN));
        }
        if (object instanceof Calendar) {
            object = ((Calendar) object).getTime(); //sets object to date, will be converted in next if-statement:
        }
        if (object instanceof Date) {
            Date date = (Date) object;
            return new SimpleDateFormat(ISO_8601_PATTERN).format(date);
        }
        if (object instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) object);
        }
        if (object instanceof char[]) {
            return new String((char[]) object);
        }
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            return toJSONObject(map);
        }
        if (object.getClass().isArray()) {
            Collection<?> c = Arrays.asList(toObjectArray(object));
            return toJSONArray(c);
        }
        if (object instanceof Collection) {
            Collection<?> coll = (Collection<?>) object;
            return toJSONArray(coll);
        }
        if (object instanceof IJsonData) {
            return ((IJsonData)object).getJson();
        }
        Log.warn("Unable to serialize object of type " + object.getClass().getName());
        throw new RuntimeException();
    }

    @SuppressWarnings("unchecked")
    default @NotNull JSONObject toJSONObject(@NotNull Map<?, ?> m) {
        JSONObject obj = new JSONObject();
        for (Map.Entry<?, ?> entry : m.entrySet()) {
            Object k = entry.getKey();
            Object value = entry.getValue();

            String key = String.valueOf(k);
            try {
                value = toJSONInstance(value);
                obj.put(key, value);
            }
            catch (Exception e){
                Log.warn("got no json value");
            }
        }
        return obj;
    }

    default @NotNull Object[] toObjectArray(@NotNull Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class<?> wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }

    @SuppressWarnings(value = "unchecked")
    default @NotNull JSONArray toJSONArray(@NotNull Collection<?> c) {
        JSONArray array = new JSONArray();
        for (Object o : c) {
            try {
                o = toJSONInstance(o);
                array.add(o);
            }
            catch (Exception e){
                Log.warn("got no json value");
            }
        }
        return array;
    }

}
