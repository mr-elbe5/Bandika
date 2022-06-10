/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.data;

import de.elbe5.log.Log;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LocalizedStrings {

    private final static Map<String, String> stringMap = new HashMap<>();

    public static void addResourceBundle(@NotNull String name, @NotNull Locale locale){
        ResourceBundle bundle = ResourceBundle.getBundle(name, locale);
        for (String key : bundle.keySet()){
            stringMap.put(key, bundle.getString(key));
        }
    }

    public static @NotNull String getString(@NotNull String key) {
        try {
            String s = stringMap.get(key);
            if (s!=null)
                return s;
        }
        catch (Exception e){
            Log.warn("string not found: " + key);
        }
        return "[" + key + "]";
    }
}
