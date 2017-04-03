/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import java.util.HashMap;
import java.util.Map;

public class ActionDispatcher {

    private static final Map<String, Class<? extends Enum>> classes = new HashMap<>();

    public static void addClass(String key, Class<? extends Enum> cls) {
        classes.put(key, cls);
    }

    public static Class<? extends Enum> getClass(String key) {
        if (!classes.containsKey(key)) {
            return null;
        }
        return classes.get(key);
    }

    public static IAction getAction(String key, String actionName) {
        Class<? extends Enum> cls = getClass(key);
        if (cls == null)
            return null;
        if (actionName.isEmpty())
            actionName = "defaultAction";
        Object obj = Enum.valueOf(cls, actionName);
        if (obj instanceof IAction)
            return (IAction) obj;
        return null;
    }
}
