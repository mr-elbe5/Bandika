/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.field;

import java.util.HashMap;
import java.util.Map;

public abstract class Fields {

    protected static Map<String, Class<?>> fieldClasses = new HashMap<>();

    public static Field getNewField(String type) {
        Class<?> cls = fieldClasses.get(type);
        try {
            if (cls != null) {
                return (Field) cls.newInstance();
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    public static void registerFieldType(String fieldType, Class<? extends Field> cls) {
        fieldClasses.put(fieldType, cls);
    }

}
