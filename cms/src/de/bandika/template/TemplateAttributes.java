/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import java.io.Serializable;
import java.util.HashMap;

public class TemplateAttributes extends HashMap<String, String> implements Serializable {

    public TemplateAttributes(String src) {
        setAttributes(src);
    }

    protected void setAttributes(String src) {
        clear();
        boolean inString = false;
        char ch;
        int lastBlank = 0;
        for (int i = 0; i < src.length(); i++) {
            ch = src.charAt(i);
            if (ch == '\"')
                inString = !inString;
            if ((ch == ' ' && !inString) || i == src.length() - 1) {
                String attributesString = (i == src.length() - 1 ? src.substring(lastBlank) : src.substring(lastBlank, i));
                int pos = attributesString.indexOf('=');
                if (pos != -1) {
                    String key = attributesString.substring(0, pos).trim();
                    String value = attributesString.substring(pos + 1).trim();
                    if (value.startsWith("\""))
                        value = value.substring(1);
                    if (value.endsWith("\""))
                        value = value.substring(0, value.length() - 1);
                    put(key, value.trim());
                }
                lastBlank = i;
            }
        }
    }

    public String getString(String key) {
        String value = get(key);
        if (value != null)
            return value;
        return "";
    }

    public int getInt(String key) {
        int value = -1;
        try {
            value = Integer.parseInt(get(key));
        } catch (Exception ignore) {
        }
        return value;
    }

}
