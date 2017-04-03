/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import java.util.*;

public class KeyValueMap extends HashMap<String, Object> {

    public String getString(String key) {
        Object obj = get(key);
        if (obj == null) return "";
        if (obj instanceof String) return (String) obj;
        if (obj instanceof String[]) return ((String[]) obj)[0];
        return null;
    }

    public String getString(String key, String def) {
        Object obj = get(key);
        if (obj == null) return def;
        if (obj instanceof String) return (String) obj;
        if (obj instanceof String[]) return ((String[]) obj)[0];
        return def;
    }

    public int getInt(String key, int defaultValue) {
        int value = defaultValue;
        try {
            String str = getString(key);
            value = Integer.parseInt(str);
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean value = defaultValue;
        try {
            String str = getString(key);
            value = Integer.parseInt(str) > 0;
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("KeyValueMap:\n");
        for (Object key : keySet()) {
            Object value = get(key);
            sb.append(key);
            sb.append('=');
            sb.append(value);
            sb.append('\n');
        }
        return sb.toString();
    }
}
